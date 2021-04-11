package com.tminus1010.budgetvalue.plans.domain

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.nullIfZero
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue._shared.date_period_getter.DatePeriodGetter
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue._core.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue._core.extensions.launch
import com.tminus1010.budgetvalue._core.extensions.withLatestFrom2
import com.tminus1010.budgetvalue.plans.data.IPlansRepo
import com.tminus1010.budgetvalue.plans.models.Plan
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import com.tminus1010.tmcommonkotlin.rx.extensions.total
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivePlanDomain @Inject constructor(
    plansRepo: IPlansRepo,
    categoriesDomain: CategoriesDomain,
    datePeriodGetter: DatePeriodGetter,
) : ViewModel(), IActivePlanDomain {
    override val activePlan = plansRepo.plans
        .flatMap {
            // If the last plan is a valid active plan, use that. Otherwise, copy some of the last plan's properties if it exists or create a new one, and push it.
            val lastPlan = it.lastOrNull()
            if (lastPlan != null && lastPlan.localDatePeriod.blockingFirst() == datePeriodGetter.currentDatePeriod())
                Observable.just(lastPlan)
            else {
                when {
                    lastPlan != null ->
                        Observable.just(
                            Plan(
                            Observable.just(datePeriodGetter.currentDatePeriod()),
                            lastPlan.amount,
                            lastPlan.categoryAmounts)
                        )
                    else ->
                        Observable.just(
                            Plan(
                            Observable.just(datePeriodGetter.currentDatePeriod()),
                            BigDecimal.ZERO,
                            emptyMap())
                        )
                }.doOnNext { plansRepo.pushPlan(it).blockingAwait() }
            }
        }
        .toBehaviorSubject()
    override val activePlanCAs =
        Rx.combineLatest(activePlan, categoriesDomain.userCategories)
            .map { (activePlan, activeCategories) ->
                activeCategories.associateWith { BigDecimal.ZERO } + activePlan.categoryAmounts
            }
            .flatMapSourceHashMap(SourceHashMap(exitValue = BigDecimal.ZERO))
            { it.itemObservableMap2 }
            .replay(1).refCount()
    override val expectedIncome = activePlan.map { it.amount }
    override val defaultAmount = activePlanCAs
        .switchMap { it.values.total() }
        .withLatestFrom(expectedIncome) { caTotal, expectedIncome -> expectedIncome - caTotal }
        .replay(1).refCount()
}