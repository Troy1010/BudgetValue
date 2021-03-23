package com.tminus1010.budgetvalue.features.plans

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.itemObservableMap2
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.extensions.withLatestFrom2
import com.tminus1010.budgetvalue.features.categories.CategoriesVM
import com.tminus1010.budgetvalue.features.categories.Category
import com.tminus1010.budgetvalue.features_shared.DatePeriodGetter
import com.tminus1010.budgetvalue.features_shared.Domain
import com.tminus1010.budgetvalue.middleware.Rx
import com.tminus1010.budgetvalue.middleware.source_objects.SourceHashMap
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import com.tminus1010.tmcommonkotlin.rx.extensions.total
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal

class ActivePlanVM(domain: Domain, categoriesVM: CategoriesVM, datePeriodGetter: DatePeriodGetter) : ViewModel() {
    val activePlan = domain.plans
        .flatMap {
            // If the last plan is a valid active plan, use that. Otherwise, copy some of the last plan's properties if it exists or create a new one, and push it.
            if (it.lastOrNull()?.localDatePeriod?.blockingFirst() == datePeriodGetter.currentDatePeriod())
                Observable.just(it.last())
            else {
                when {
                    it.lastOrNull() != null ->
                        Observable.just(Plan(Observable.just(datePeriodGetter.currentDatePeriod()),
                            it.last().defaultAmount,
                            it.last().categoryAmounts))
                    else ->
                        Observable.just(Plan(Observable.just(datePeriodGetter.currentDatePeriod()),
                            BigDecimal.ZERO,
                            emptyMap()))
                }.doOnNext { domain.pushPlan(it).blockingAwait() }
            }
        }
        .toBehaviorSubject()
    val intentPushExpectedIncome = PublishSubject.create<BigDecimal>()
        .also {
            it.withLatestFrom2(activePlan)
                .launch { (amount, plan) -> domain.updatePlanAmount(plan, amount) }
        }
    val intentPushPlanCA = PublishSubject.create<Pair<Category, BigDecimal?>>()
        .also {
            it.withLatestFrom2(activePlan)
                .launch { (amount, plan) -> domain.updatePlanCA(plan, amount) }
        }
    val activePlanCAs =
        Rx.combineLatest(activePlan, categoriesVM.userCategories)
            .scan(SourceHashMap<Category, BigDecimal>(exitValue = BigDecimal(0))) { acc, (activePlan, activeCategories) ->
                activeCategories
                    .associateWith { BigDecimal.ZERO }
                    .let { it + activePlan.categoryAmounts }
                    .also { acc.adjustTo(it) }
                acc
            }
            .skip(1)
            .toBehaviorSubject()
    val planUncategorized = activePlanCAs.itemObservableMap2()
        .switchMap { it.values.total() }
        .replay(1).refCount()
    val expectedIncome = intentPushExpectedIncome
        .startWith(activePlan.take(1).map { it.defaultAmount })
        .toBehaviorSubject()
    val defaultAmount = Rx.combineLatest(expectedIncome, planUncategorized)
        .map { it.first - it.second }
}