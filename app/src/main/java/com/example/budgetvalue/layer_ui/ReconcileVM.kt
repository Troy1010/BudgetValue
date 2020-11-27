package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.*
import com.example.budgetvalue.extensions.toSourceHashMap
import com.example.budgetvalue.extensions.withLatestFrom
import com.example.budgetvalue.layer_data.Repo
import com.example.budgetvalue.model_app.Category
import com.example.budgetvalue.model_app.ReconcileRowData
import com.example.budgetvalue.model_app.Transaction
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
        .withLatestFrom(repo.fetchReconcileCategoryAmounts())
        .map { (a,b) -> mapReconcileCategoryAmounts(a,b) }
        .doOnNext(::bindReconcileCategoryAmountsToRepo)
        .toBehaviorSubject()
    val rowDatas = zip(activeCategories, reconcileCategoryAmounts, planVM.planCategoryAmounts, transactionSet) // TODO("Is this zipping correctly..?")
        .map { getRowDatas(it.first, it.second, it.third, it.fourth) }
    val reconcileDefault = reconcileCategoryAmounts
        .switchMap { it.observable }
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
                planCA.observable.value[category] ?: Observable.just(BigDecimal.ZERO),
                BehaviorSubject.createDefault(transactionSet.map { it.categoryAmounts[category] ?: BigDecimal.ZERO }.sum()),
                reconcileCA.observable.value[category]!!
            )
        }
    }

    fun mapReconcileCategoryAmounts(activeCategories: Iterable<Category>, oldReconcileCategoryAmounts:Map<Category, BigDecimal>): SourceHashMap<Category, BigDecimal> {
        return activeCategories
            .associateWith { oldReconcileCategoryAmounts[it] ?: BigDecimal.ZERO }
            .toSourceHashMap()
    }

    fun getActiveCategories(transactionSet: Iterable<Transaction>): HashSet<Category> {
        return transactionSet
            .fold(HashSet()) { acc, transaction -> acc.addAll(transaction.categoryAmounts.keys); acc }
    }

    fun bindReconcileCategoryAmountsToRepo(reconcileCategoryAmounts: SourceHashMap<Category, BigDecimal>) {
        // TODO("move this logic to Repo")
        reconcileCategoryAmounts.observable // TODO("Handle disposables")
            .subscribe { ca -> ca.forEach { kv -> kv.value.skip(1).subscribe { repo.pushReconcileCategoryAmounts(ca.mapValues { it.value.value }) } } }
    }
}
