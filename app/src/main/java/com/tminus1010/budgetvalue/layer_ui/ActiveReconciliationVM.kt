package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.categoryComparator
import com.tminus1010.budgetvalue.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.combineLatestAsTuple
import com.tminus1010.budgetvalue.extensions.sum
import com.tminus1010.budgetvalue.extensions.total
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.mergeCombineWithIndex
import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.budgetvalue.model_app.ReconcileRowData
import com.tminus1010.budgetvalue.model_app.Reconciliation
import com.tminus1010.budgetvalue.model_app.Transaction
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import java.time.LocalDate

class ActiveReconciliationVM(
    private val repo: Repo,
    private val transactionSet: Observable<List<Transaction>>,
    private val accountsTotal: Observable<BigDecimal>,
    private val activePlanVM: ActivePlanVM,
) : ViewModel() {
    val intentSaveReconciliation:PublishSubject<Unit> = PublishSubject.create<Unit>()
        .also {
            it
                .observeOn(Schedulers.io())
                .flatMap { accountsTotal }
                .map { accountsTotal ->
                    Reconciliation(LocalDate.now(),
                        accountsTotal,
                        activeReconcileCAs.value.filter { it.value != BigDecimal(0) },)
                }
                .doOnNext { repo.pushReconciliation(it).blockingAwait() }
                .doOnNext { clearActiveReconciliation.onNext(Unit) }
                .subscribe()
        }
    val intentPushActiveReconcileCA = PublishSubject.create<Pair<Category, BigDecimal>>()
        .also { it.subscribeOn(Schedulers.io()).subscribe(repo::pushActiveReconcileCA) }
    // *Normally, doing pushActiveReconcileCAs would trigger fetchActiveReconcileCAs.. but since
    // sharedPrefs does not support Observables, fetchActiveReconcileCAs is cold, so this subject is a workaround.
    val clearActiveReconciliation = PublishSubject.create<Unit>()
        .also {
            it
                .subscribeOn(Schedulers.io())
                .subscribe { repo.pushActiveReconcileCAs(null) }
        }
    // # State
    val activeCategories = transactionSet
        .map(::getActiveCategories)
        .toBehaviorSubject()
    val activeReconcileCAs = mergeCombineWithIndex(
        Observable.just(repo.fetchActiveReconcileCAs()),
        intentPushActiveReconcileCA,
        activeCategories,
        clearActiveReconciliation,
    )
        .scan(SourceHashMap<Category, BigDecimal>(exitValue = BigDecimal(0))) { acc, (i, activeReconcileCAs, activeReconcileCA, activeCategories, _) ->
            when (i) {
                0 -> { activeReconcileCAs!!
                    acc.clear()
                    acc.putAll(activeReconcileCAs)
                    if (activeCategories!=null)
                        acc.putAll(activeCategories
                            .filter { it !in acc.keys }
                            .associate { it to BigDecimal.ZERO })
                }
                1 -> { activeReconcileCA!!.also { (k, v) -> acc[k] = v } }
                2 -> { activeCategories!!
                    acc.putAll(activeCategories
                        .filter { it !in acc.keys }
                        .associate { it to BigDecimal.ZERO })
                }
                3 -> {
                    acc.clear()
                    if (activeCategories!=null)
                        acc.putAll(activeCategories
                            .filter { it !in acc.keys }
                            .associate { it to BigDecimal.ZERO })
                }
            }
            acc
        }
        .toBehaviorSubject()
    val rowDatas = combineLatestAsTuple(activeCategories, activeReconcileCAs.value.itemObservableMap2, activePlanVM.planCAs, transactionSet)
        .map { getRowDatas(it.first, it.second, it.third, it.fourth) }
    val activeReconcileTotal = activeReconcileCAs.value.itemObservableMap2
        .switchMap { it.values.total() }
    val budgetedUncategorized = combineLatestAsTuple(accountsTotal, rowDatas.flatMap { it.map { it.budgeted }.total() })
        .map { it.first - it.second }
    val defaultAmount = combineLatestAsTuple(accountsTotal, activeReconcileTotal, budgetedUncategorized)
        .map { it.first - it.second - it.third } // TODO("This might not be right, but first the budgeted column should be fixed")
    
    //
    fun getRowDatas(
        activeCategories: Iterable<Category>,
        reconcileCA: Map<Category, BehaviorSubject<BigDecimal>>,
        planCA: SourceHashMap<Category, BigDecimal>,
        transactionSet: List<Transaction>
    ): Iterable<ReconcileRowData> {
        return activeCategories.map { category ->
            ReconcileRowData(
                category,
                planCA.itemObservableMap.value[category] ?: Observable.just(BigDecimal.ZERO),
                Observable.just(transactionSet.map { it.categoryAmounts[category] ?: BigDecimal.ZERO }.sum()),
                reconcileCA[category] ?: Observable.just(BigDecimal.ZERO),
            )
        }
    }

    fun getActiveCategories(transactionSet: Iterable<Transaction>): List<Category> {
        return transactionSet
            .fold(HashSet<Category>()) { acc, transaction -> acc.addAll(transaction.categoryAmounts.keys); acc }
            .sortedWith(categoryComparator)
    }
}
