package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.categoryComparator
import com.tminus1010.budgetvalue.combineLatestAsTuple
import com.tminus1010.budgetvalue.extensions.total
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.mergeCombineWithIndex
import com.tminus1010.budgetvalue.model_data.Category
import com.tminus1010.budgetvalue.model_app.Plan
import com.tminus1010.budgetvalue.source_objects.SourceHashMap
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import java.time.LocalDate

class ActivePlanVM(val repo: Repo, datePeriodGetter: DatePeriodGetter) : ViewModel() {
    val intentPushExpectedIncome = PublishSubject.create<BigDecimal>()
        .also { it.subscribe(repo::pushExpectedIncome) }
    val intentSaveActivePlan = PublishSubject.create<Unit>()
        .also {
            it
                .map {
                    Plan(datePeriodGetter.getDatePeriodObservable(LocalDate.now()),
                        expectedIncome.value,
                        repo.activePlan.blockingFirst()
                    )
                }
                .flatMapCompletable { repo.pushPlan(it) }
                .subscribe()
        }
    val intentPushPlanCA = PublishSubject.create<Pair<Category, BigDecimal>>()
        .also {
            it.subscribe { repo.pushActivePlanCA(it) }
            // The last plan might be the active plan, if it contains the current date.
            // push to there as well.
            it
                .withLatestFrom(repo.plans) { _, b -> b }
                .filter { it.isNotEmpty() }
                .map { it.last() }
                .filter { LocalDate.now() in it.localDatePeriod.blockingFirst() }
                .map { Unit }
                .subscribe(intentSaveActivePlan)
        }

    val activePlan = mergeCombineWithIndex(
        repo.activePlan.take(1),
        intentPushPlanCA,
        repo.activeCategories
    )
        .scan(SourceHashMap<Category, BigDecimal>(exitValue = BigDecimal(0))) { acc, (i, activePlan, intentPushPlanCA, activeCategories) ->
            when (i) {
                0 -> { activePlan!!
                    acc.clear()
                    acc.putAll(activePlan)
                    if (activeCategories!=null)
                        acc.putAll(activeCategories
                            .filter { it !in acc.keys }
                            .associate { it to BigDecimal.ZERO })
                }
                1 -> { intentPushPlanCA!!.also { (k, v) -> acc[k] = v } }
                2 -> { activeCategories!!
                    acc.putAll(activeCategories
                        .filter { it !in acc.keys }
                        .associate { it to BigDecimal.ZERO })
                }
            }
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