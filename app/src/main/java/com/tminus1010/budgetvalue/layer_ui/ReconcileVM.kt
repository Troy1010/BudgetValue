package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.SourceHashMap
import com.tminus1010.budgetvalue.combineLatestAsTuple
import com.tminus1010.budgetvalue.extensions.total
import com.tminus1010.budgetvalue.extensions.withLatestFrom
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.budgetvalue.model_app.ReconcileRowData
import com.tminus1010.budgetvalue.model_app.Transaction
import com.tminus1010.budgetvalue.extensions.sum
import com.tminus1010.budgetvalue.zip
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal

class ReconcileVM(
    private val repo: Repo,
    private val transactionSet: Observable<List<Transaction>>,
    private val accountsTotal: Observable<BigDecimal>,
    private val planVM: PlanVM
) : ViewModel() {
    val activeCategories = transactionSet
        .map(::getActiveCategories)
        .toBehaviorSubject()
    val reconcileCategoryAmounts = repo.activeReconcileCategoryAmounts
    val rowDatas = zip(activeCategories, reconcileCategoryAmounts, planVM.statePlanCAs, transactionSet) // TODO("Is this zipping correctly..?")
        .map { getRowDatas(it.first, it.second, it.third, it.fourth) }
    val reconcileDefault = reconcileCategoryAmounts
        .switchMap { it.observable }
        .flatMap { it.values.total() }
        .withLatestFrom(accountsTotal)
        .map { it.second - it.first } // TODO("Should subtract previous accountsTotal")
    val uncategorizedBudgeted = combineLatestAsTuple(accountsTotal, rowDatas.flatMap { it.map { it.budgeted }.total() })
        .map { it.first - it.second }

    fun getRowDatas(
        activeCategories: Iterable<Category>,
        reconcileCA: SourceHashMap<Category, BigDecimal>,
        planCA: SourceHashMap<Category, BigDecimal>,
        transactionSet: List<Transaction>
    ): Iterable<ReconcileRowData> {
        return activeCategories.map { category ->
            ReconcileRowData(
                category,
                planCA.observable.value[category] ?: Observable.just(BigDecimal.ZERO),
                Observable.just(transactionSet.map { it.categoryAmounts[category] ?: BigDecimal.ZERO }.sum()),
                reconcileCA.observable.value[category]!!
            )
        }
    }

    fun getActiveCategories(transactionSet: Iterable<Transaction>): HashSet<Category> {
        return transactionSet
            .fold(HashSet()) { acc, transaction -> acc.apply { addAll(transaction.categoryAmounts.keys) } }
    }

    init {
        // # Bind activeCategories -> reconcileCategoryAmounts
        combineLatestAsTuple(activeCategories, reconcileCategoryAmounts)
            .subscribeOn(Schedulers.io())
            .subscribe { (activeCategories, reconcileCategoryAmounts) ->
                activeCategories.asSequence()
                    .filter { it !in reconcileCategoryAmounts }
                    .forEach { reconcileCategoryAmounts[it] = BigDecimal.ZERO }
            }
    }
}
