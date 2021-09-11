package com.tminus1010.budgetvalue.plans.domain

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue._shared.date_period_getter.DatePeriodGetter
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.plans.models.Plan
import com.tminus1010.tmcommonkotlin.rx.extensions.total
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivePlanDomain @Inject constructor(
    plansRepo: PlansRepo,
    datePeriodGetter: DatePeriodGetter,
) : ViewModel() {
    val activePlan = plansRepo.plans
        .flatMap {
            // If the last plan is a valid active plan, use that. Otherwise, copy some of the last plan's properties if it exists or create a new one, and push it.
            val lastPlan = it.lastOrNull()
            if (lastPlan != null && lastPlan.localDatePeriod == datePeriodGetter.currentDatePeriod())
                Observable.just(lastPlan)
            else {
                when {
                    lastPlan != null ->
                        Observable.just(
                            Plan(
                                datePeriodGetter.currentDatePeriod(),
                                lastPlan.amount,
                                lastPlan.categoryAmounts
                            )
                        )
                    else ->
                        Observable.just(
                            Plan(
                                datePeriodGetter.currentDatePeriod(),
                                BigDecimal.ZERO,
                                emptyMap()
                            )
                        )
                }.doOnNext { plansRepo.pushPlan(it).blockingAwait() }
            }
        }
        .replay(1).refCount()
    val activePlanCAs =
        activePlan.map { it.categoryAmounts }
            .flatMapSourceHashMap(SourceHashMap(exitValue = BigDecimal.ZERO))
            { it.itemObservableMap }
            .replay(1).refCount()
    val expectedIncome = activePlan.map { it.amount }
        .distinctUntilChanged()
    val defaultAmount =
        Rx.combineLatest(
            expectedIncome,
            activePlanCAs.switchMap { it.values.total() },
        ).map { it.first - it.second }
            .replay(1).refCount()
}