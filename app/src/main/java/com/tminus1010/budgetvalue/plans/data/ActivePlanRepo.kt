package com.tminus1010.budgetvalue.plans.data

import com.tminus1010.budgetvalue._core.data.repos.CurrentDatePeriodRepo
import com.tminus1010.budgetvalue._core.all.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue._core.framework.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.plans.domain.Plan
import com.tminus1010.tmcommonkotlin.rx.extensions.total
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivePlanRepo @Inject constructor(
    plansRepo: PlansRepo,
    currentDatePeriodRepo: CurrentDatePeriodRepo,
) {
    val activePlan =
        plansRepo.plans
            .flatMap {
                // If the last plan is a valid active plan, use that. Otherwise, copy some of the last plan's properties if it exists or create a new one, and push it.
                val lastPlan = it.lastOrNull()
                if (lastPlan != null && lastPlan.localDatePeriod == currentDatePeriodRepo.currentDatePeriod.value)
                    Observable.just(lastPlan)
                else {
                    when {
                        lastPlan != null ->
                            Observable.just(
                                Plan(
                                    currentDatePeriodRepo.currentDatePeriod.value,
                                    lastPlan.amount,
                                    lastPlan.categoryAmounts
                                )
                            )
                        else ->
                            Observable.just(
                                Plan(
                                    currentDatePeriodRepo.currentDatePeriod.value,
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
    val expectedIncome =
        activePlan.map { it.amount }
            .distinctUntilChanged()
    val defaultAmount =
        Observable.combineLatest(expectedIncome, activePlanCAs.switchMap { it.values.total() })
        { expectedIncome, activePlanTotal ->
            expectedIncome - activePlanTotal
        }
            .replay(1).refCount()
}