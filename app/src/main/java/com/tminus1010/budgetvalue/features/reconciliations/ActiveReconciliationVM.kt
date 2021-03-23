package com.tminus1010.budgetvalue.features.reconciliations

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.middleware.Rx
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.features.categories.CategoriesVM
import com.tminus1010.budgetvalue.features_shared.Domain
import com.tminus1010.budgetvalue.features.plans.ActivePlanVM
import com.tminus1010.budgetvalue.features.transactions.TransactionsVM
import com.tminus1010.budgetvalue.features.categories.Category
import com.tminus1010.budgetvalue.features.transactions.Transaction
import com.tminus1010.budgetvalue.middleware.source_objects.SourceHashMap
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import com.tminus1010.tmcommonkotlin.rx.extensions.total
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import java.time.LocalDate

class ActiveReconciliationVM(
    private val domain: Domain,
    transactionsVM: TransactionsVM,
    activePlanVM: ActivePlanVM,
    categoriesVM: CategoriesVM,
) : ViewModel() {
    val intentPushActiveReconcileCA = PublishSubject.create<Pair<Category, BigDecimal>>()
        .also { it.launch { domain.pushActiveReconciliationCA(it) } }
    // # State
    val activeReconcileCAs =
        Rx.combineLatest(domain.activeReconciliationCAs, categoriesVM.userCategories)
            .scan(SourceHashMap<Category, BigDecimal>(exitValue = BigDecimal(0))) { acc, (activeReconcileCAs, activeCategories) ->
                activeCategories
                    .associateWith { BigDecimal.ZERO }
                    .let { it + activeReconcileCAs }
                    .also { acc.adjustTo(it) }
                acc
            }
            .toBehaviorSubject()
    val rowDatas = Rx.combineLatest(categoriesVM.userCategories, activeReconcileCAs.value.itemObservableMap2, activePlanVM.activePlanCAs, transactionsVM.spends)
        .map { getRowDatas(it.first, it.second, it.third, it.fourth) }
    val caTotal = activeReconcileCAs.value.itemObservableMap2
        .switchMap { it.values.total() }
        .replay(1).refCount()

    //
    fun getRowDatas(
        activeCategories: Iterable<Category>,
        reconcileCAs: Map<Category, BehaviorSubject<BigDecimal>>,
        activePlanCAs: SourceHashMap<Category, BigDecimal>,
        transactions: List<Transaction>
    ): Iterable<ReconcileRowData> {
        return activeCategories.map { category ->
            ReconcileRowData(
                category,
                activePlanCAs.itemObservableMap.value[category] ?: Observable.just(BigDecimal.ZERO),
                Observable.just(transactions.filter { it.date in domain.getDatePeriod(
                    LocalDate.now()) }.map { it.categoryAmounts[category] ?: BigDecimal.ZERO }.sum()),
                reconcileCAs[category] ?: Observable.just(BigDecimal.ZERO),
            )
        }
    }
}
