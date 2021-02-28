package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.combineLatestAsTuple
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.layer_domain.Domain
import com.tminus1010.budgetvalue.model_data.Category
import com.tminus1010.budgetvalue.model_domain.Plan
import com.tminus1010.budgetvalue.source_objects.SourceHashMap
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import com.tminus1010.tmcommonkotlin.rx.extensions.total
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import java.time.LocalDate

class ActivePlanVM(repo: Repo, domain: Domain) : ViewModel() {
    val intentPushExpectedIncome = PublishSubject.create<BigDecimal>()
        .also { it.subscribe(repo::pushExpectedIncome) }
    val intentSaveActivePlan = PublishSubject.create<Unit>()
        .also {
            it
                .map {
                    Plan(domain.getDatePeriodObservable(LocalDate.now()),
                        expectedIncome.value,
                        repo.activePlanCAs.blockingFirst()
                    )
                }
                .flatMapCompletable { domain.pushPlan(it) }
                .subscribe()
        }
    val intentPushPlanCA = PublishSubject.create<Pair<Category, BigDecimal>>()
        .also {
            it.subscribe { repo.pushActivePlanCA(it) }
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
    val activePlan =
        combineLatestAsTuple(repo.activePlanCAs, repo.activeCategories)
            .scan(SourceHashMap<Category, BigDecimal>(exitValue = BigDecimal(0))) { acc, (activeReconcileCAs, activeCategories) ->
                activeCategories
                    .associateWith { BigDecimal.ZERO }
                    .let { it + activeReconcileCAs }
                    .also { acc.adjustTo(it) }
                acc
            }
            .toBehaviorSubject()
    val planUncategorized = activePlan.value.itemObservableMap2
        .switchMap { it.values.total() }
        .replay(1).refCount()
    val expectedIncome = intentPushExpectedIncome
        .startWithItem(repo.fetchExpectedIncome())
        .toBehaviorSubject()
    val defaultAmount = combineLatestAsTuple(expectedIncome, planUncategorized)
        .map { it.first - it.second }
}