package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.combineLatestAsTuple
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.layer_domain.Domain
import com.tminus1010.budgetvalue.model_domain.Category
import com.tminus1010.budgetvalue.model_domain.ReconcileRowData
import com.tminus1010.budgetvalue.model_domain.Reconciliation
import com.tminus1010.budgetvalue.model_domain.Transaction
import com.tminus1010.budgetvalue.source_objects.SourceHashMap
import com.tminus1010.tmcommonkotlin.rx.extensions.sum
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import com.tminus1010.tmcommonkotlin.rx.extensions.total
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import java.time.LocalDate

class ActiveReconciliationVM(
    private val domain: Domain,
    private val transactionsVM: TransactionsVM,
    private val accountsVM: AccountsVM,
    private val activePlanVM: ActivePlanVM,
) : ViewModel() {
    val intentSaveReconciliation:PublishSubject<Unit> = PublishSubject.create<Unit>()
        .also {
            it
                .observeOn(Schedulers.io())
                .flatMap { accountsVM.accountsTotal }
                .map { accountsTotal ->
                    Reconciliation(LocalDate.now(),
                        accountsTotal,
                        activeReconcileCAs.value.filter { it.value != BigDecimal(0) },)
                }
                .doOnNext { domain.pushReconciliation(it).blockingAwait() }
                .doOnNext { domain.clearActiveReconcileCAs() }
                .subscribe()
        }
    val intentPushActiveReconcileCA = PublishSubject.create<Pair<Category, BigDecimal>>()
        .also { it.launch { domain.pushActiveReconciliationCA(it) } }
    // # State
    val activeReconcileCAs =
        combineLatestAsTuple(domain.activeReconciliationCAs, domain.activeCategories)
            .scan(SourceHashMap<Category, BigDecimal>(exitValue = BigDecimal(0))) { acc, (activeReconcileCAs, activeCategories) ->
                activeCategories
                    .associateWith { BigDecimal.ZERO }
                    .let { it + activeReconcileCAs }
                    .also { acc.adjustTo(it) }
                acc
            }
            .toBehaviorSubject()
    val rowDatas = combineLatestAsTuple(domain.activeCategories, activeReconcileCAs.value.itemObservableMap2, activePlanVM.activePlan, transactionsVM.spends)
        .map { getRowDatas(it.first, it.second, it.third, it.fourth) }
    val caTotal = activeReconcileCAs.value.itemObservableMap2
        .switchMap { it.values.total() }
        .replay(1).refCount()

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
}
