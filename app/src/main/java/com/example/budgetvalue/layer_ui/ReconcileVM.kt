package com.example.budgetvalue.layer_ui

import androidx.lifecycle.*
import com.example.budgetvalue.*
import com.example.budgetvalue.extensions.toSourceHashMap
import com.example.budgetvalue.layer_data.Repo
import com.example.budgetvalue.model_app.ReconcileRowData
import com.example.budgetvalue.layer_ui.misc.sum
import com.example.budgetvalue.model_app.Category
import com.example.budgetvalue.model_data.ReconcileCategoryAmounts
import com.example.budgetvalue.model_data.Transaction
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class ReconcileVM(
    private val repo: Repo,
    private val categoriesVM: CategoriesVM,
    private val transactionSet: Observable<List<Transaction>>,
    private val accountsTotal: Observable<BigDecimal>,
    private val planVM: PlanVM
) : ViewModel() {
    val activeCategories = transactionSet
        .map(::getActiveCategories)
    val reconcileCategoryAmounts = activeCategories
        .map(::getReconcileCategoryAmounts)
        .doOnNext(::bindReconcileCategoryAmountsToRepo)
        .toBehaviorSubject()
    val rowDatas = zip(transactionSet, activeCategories, reconcileCategoryAmounts, planVM.planCategoryAmounts.itemObservablesObservable)
        .map { getRowDatas(it.first, it.second, it.third, it.fourth) }
    val uncategorizedSpent = transactionSet
        .map { -it.map { it.uncategorizedAmounts }.sum() }
    val uncategorizedActual = combineLatestAsTuple(accountsTotal, uncategorizedSpent, planVM.uncategorizedPlan)
        .map { it.first - it.second - it.third } // TODO("Should subtract from last block")
    val uncategorizedReconcile = reconcileCategoryAmounts
        .switchMap { it.itemObservablesObservable }
        .flatMap { getTotalObservable(it.values) }
        .map { -it }
    val uncategorizedBudgeted = combineLatestAsTuple(uncategorizedActual, uncategorizedReconcile)
        .map { it.first + it.second }

    fun getRowDatas(
        transactionSet: List<Transaction>,
        activeCategories: Iterable<Category>,
        reconcileCA: SourceHashMap<Category, BigDecimal>,
        planCA: HashMap<String, BehaviorSubject<BigDecimal>>
    ): ArrayList<ReconcileRowData> {
        val rowDatas = ArrayList<ReconcileRowData>()
        for (category in activeCategories) {
            val spent = transactionSet.map { it.categoryAmounts[category.name] ?: BigDecimal.ZERO }.sum()
            val planAmount = planCA[category.name]!!
            val actual = planAmount
                .map { it + spent }
                .toBehaviorSubject()
            rowDatas.add(ReconcileRowData(
                category,
                actual,
                reconcileCA.itemObservablesObservable.value[category]!!
            ))
        }
        return rowDatas
    }

    fun getReconcileCategoryAmounts(activeCategories: Iterable<Category>): SourceHashMap<Category, BigDecimal> {
        val oldReconcileCategoryAmounts = repo.fetchReconcileCategoryAmounts().associate { it.categoryName to it.amount }
        return activeCategories
            .associateWith { oldReconcileCategoryAmounts[it.name] ?: BigDecimal.ZERO }
            .toSourceHashMap()
    }

    fun getActiveCategories(transactionSet: Iterable<Transaction>): HashSet<Category> {
        fun getCategories(transaction: Transaction): Iterable<Category> {
            return transaction.categoryAmounts.keys.map { categoriesVM.getCategoryByName(it) }
        }
        return transactionSet
            .fold(HashSet()) { acc, transaction -> acc.addAll(getCategories(transaction)); acc }
    }

    fun bindReconcileCategoryAmountsToRepo(reconcileCategoryAmounts: SourceHashMap<Category, BigDecimal>) {
        reconcileCategoryAmounts.observable // TODO("Handle disposables")
            .subscribe { repo.pushReconcileCategoryAmounts(it.map { ReconcileCategoryAmounts(it.key.name, it.value) }) }
    }
}
