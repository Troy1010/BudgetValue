package com.tminus1010.budgetvalue.plans

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue.extensions.withLatestFrom2
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue._core.shared_features.date_period_getter.DatePeriodGetter
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.nullIfZero
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import com.tminus1010.tmcommonkotlin.rx.extensions.total
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ActivePlanVM @Inject constructor(
    domainFacade: DomainFacade,
    categoriesVM: CategoriesVM,
    datePeriodGetter: DatePeriodGetter,
    plansVM: PlansVM
) : ViewModel() {
    val activePlan = plansVM.plans
        .flatMap {
            // If the last plan is a valid active plan, use that. Otherwise, copy some of the last plan's properties if it exists or create a new one, and push it.
            val lastPlan = it.lastOrNull()
            if (lastPlan != null && lastPlan.localDatePeriod.blockingFirst() == datePeriodGetter.currentDatePeriod())
                Observable.just(lastPlan)
            else {
                when {
                    lastPlan != null ->
                        Observable.just(Plan(Observable.just(datePeriodGetter.currentDatePeriod()),
                            lastPlan.amount,
                            lastPlan.categoryAmounts))
                    else ->
                        Observable.just(Plan(Observable.just(datePeriodGetter.currentDatePeriod()),
                            BigDecimal.ZERO,
                            emptyMap()))
                }.doOnNext { domainFacade.pushPlan(it).blockingAwait() }
            }
        }
        .toBehaviorSubject()
    val intentPushExpectedIncome = PublishSubject.create<BigDecimal>()
        .also {
            it.withLatestFrom2(activePlan)
                .launch { (amount, plan) -> domainFacade.updatePlanAmount(plan, amount) }
        }
    val intentPushActivePlanCA = PublishSubject.create<Pair<Category, BigDecimal?>>()
        .also {
            it.withLatestFrom2(activePlan)
                .launch { (categoryAmount, plan) -> domainFacade.updatePlanCA(plan, categoryAmount.first, categoryAmount.second?.nullIfZero()) }
        }
    val activePlanCAs =
        Rx.combineLatest(activePlan, categoriesVM.userCategories)
            .map { (activePlan, activeCategories) ->
                activeCategories.associateWith { BigDecimal.ZERO } + activePlan.categoryAmounts
            }
            .flatMapSourceHashMap(SourceHashMap(exitValue = BigDecimal.ZERO))
            { it.itemObservableMap2 }
            .replay(1).refCount()
    val planUncategorized = activePlanCAs
        .switchMap { it.values.total() }
        .replay(1).refCount()
    val expectedIncome = intentPushExpectedIncome
        .startWith(activePlan.take(1).map { it.amount })
        .toBehaviorSubject()
    val defaultAmount = Rx.combineLatest(expectedIncome, planUncategorized)
        .map { it.first - it.second }
}