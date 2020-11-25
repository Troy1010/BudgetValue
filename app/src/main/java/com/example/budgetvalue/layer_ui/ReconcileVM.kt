package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.SourceHashMap
import com.example.budgetvalue.combineLatestAsTuple
import com.example.budgetvalue.extensions.toSourceHashMap
import com.example.budgetvalue.extensions.withLatestFrom
import com.example.budgetvalue.getTotalObservable
import com.example.budgetvalue.layer_data.Repo
import com.example.budgetvalue.layer_ui.misc.sum
import com.example.budgetvalue.model_app.Category
import com.example.budgetvalue.model_app.ReconcileRowData
import com.example.budgetvalue.model_app.Transaction
import com.example.budgetvalue.model_data.ReconcileCategoryAmounts
import com.example.budgetvalue.zip
import com.tminus1010.tmcommonkotlin.tuple.Quadruple
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
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
    val reconcileCategoryAmounts = activeCategories
        .map(::getReconcileCategoryAmounts)
        .doOnNext(::bindReconcileCategoryAmountsToRepo)
        .toBehaviorSubject()
    val rowDatas = zip(activeCategories, reconcileCategoryAmounts, planVM.planCategoryAmounts.observable, transactionSet)
        .map { getRowDatas(it.first, it.second, it.third, it.fourth) }
    val reconcileDefault = reconcileCategoryAmounts
        .switchMap { it.itemObservablesObservable }
        .flatMap { getTotalObservable(it.values) }
        .withLatestFrom(accountsTotal)
        .map { it.second - it.first } // TODO("Should subtract previous accountsTotal")
    val uncategorizedBudgeted = combineLatestAsTuple(accountsTotal, rowDatas.flatMap { getTotalObservable(it.map { it.budgeted }) })
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
                planCA.itemObservablesObservable.value[category]!!,
                BehaviorSubject.createDefault(transactionSet.map { it.categoryAmounts[category] ?: BigDecimal.ZERO }.sum()),
                reconcileCA.itemObservablesObservable.value[category]!!
            )
        }
    }

    fun getReconcileCategoryAmounts(activeCategories: Iterable<Category>): SourceHashMap<Category, BigDecimal> {
        val oldReconcileCategoryAmounts = repo.fetchReconcileCategoryAmounts().associate { it.categoryName to it.amount }
        return activeCategories
            .associateWith { oldReconcileCategoryAmounts[it.name] ?: BigDecimal.ZERO }
            .toSourceHashMap()
    }

    fun getActiveCategories(transactionSet: Iterable<Transaction>): HashSet<Category> {
        return transactionSet
            .fold(HashSet()) { acc, transaction -> acc.addAll(transaction.categoryAmounts.keys); acc }
    }

    fun bindReconcileCategoryAmountsToRepo(reconcileCategoryAmounts: SourceHashMap<Category, BigDecimal>) {
        reconcileCategoryAmounts.observable // TODO("Handle disposables")
            .subscribe { repo.pushReconcileCategoryAmounts(it.map { ReconcileCategoryAmounts(it.key.name, it.value) }) }
    }
}
