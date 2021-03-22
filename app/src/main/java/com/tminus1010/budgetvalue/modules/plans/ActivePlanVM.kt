package com.tminus1010.budgetvalue.modules.plans

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.Rx
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.modules_shared.Domain
import com.tminus1010.budgetvalue.modules.categories.Category
import com.tminus1010.budgetvalue.middleware.source_objects.SourceHashMap
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import com.tminus1010.tmcommonkotlin.rx.extensions.total
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import java.time.LocalDate

class ActivePlanVM(domain: Domain) : ViewModel() {
    val intentPushExpectedIncome = PublishSubject.create<BigDecimal>()
        .also { it.launch { domain.pushExpectedIncome(it) } }
    val intentSaveActivePlan = PublishSubject.create<Unit>()
        .also {
            it
                .map {
                    Plan(domain.getDatePeriodObservable(LocalDate.now()),
                        expectedIncome.value,
                        domain.activePlanCAs.blockingFirst()
                    )
                }
                .flatMapCompletable { domain.pushPlan(it) }
                .subscribe()
        }
    val intentPushPlanCA = PublishSubject.create<Pair<Category, BigDecimal>>()
        .also {
            it.launch { domain.pushActivePlanCA(it) }
            // The last plan might be the active plan, if it contains the current date.
            // push to there as well.
            it
                .withLatestFrom(domain.plans) { _, b -> b }
                .filter { it.isNotEmpty() }
                .map { it.last() }
                .filter { LocalDate.now() in it.localDatePeriod.blockingFirst() }
                .map { Unit }
                .subscribe(intentSaveActivePlan)
        }
    val activePlanCAs =
        Rx.combineLatest(domain.activePlanCAs, domain.userCategories)
            .scan(SourceHashMap<Category, BigDecimal>(exitValue = BigDecimal(0))) { acc, (activeReconcileCAs, activeCategories) ->
                activeCategories
                    .associateWith { BigDecimal.ZERO }
                    .let { it + activeReconcileCAs }
                    .also { acc.adjustTo(it) }
                acc
            }
            .toBehaviorSubject()
    val planUncategorized = activePlanCAs.value.itemObservableMap2
        .switchMap { it.values.total() }
        .replay(1).refCount()
    val expectedIncome = intentPushExpectedIncome
        .startWithItem(domain.fetchExpectedIncome())
        .toBehaviorSubject()
    val defaultAmount = Rx.combineLatest(expectedIncome, planUncategorized)
        .map { it.first - it.second }
}