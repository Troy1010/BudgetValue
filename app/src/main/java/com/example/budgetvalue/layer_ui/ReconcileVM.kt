package com.example.budgetvalue.layer_ui

import androidx.lifecycle.*
import com.example.budgetvalue.*
import com.example.budgetvalue.layer_data.Repo
import com.example.budgetvalue.layer_ui.misc.ReconcileRowData
import com.example.budgetvalue.layer_ui.misc.sum
import com.example.budgetvalue.model_app.Category
import com.example.budgetvalue.model_data.IncomeCategoryAmounts
import com.example.budgetvalue.model_data.Transaction
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashSet

class ReconcileVM(
    private val repo: Repo,
    private val categoriesVM: CategoriesVM,
    private val transactionSet: BehaviorSubject<List<Transaction>>,
    private val accountsTotal: BehaviorSubject<BigDecimal>
) : ViewModel() {
    val activeCategories = transactionSet
        .map(::getActiveCategories)
    val incomeCategoryAmounts = activeCategories
        .map(::getIncomeCA)
        .doOnNext(::bindIncomeCAToRepo)
        .toBehaviorSubject()
    val incomeCATotal = incomeCategoryAmounts
        .map { it.map{ it.value }.sum() }
    val rowDatas = zip(transactionSet, activeCategories, incomeCategoryAmounts)
        .map { getRowDatas(it.first, it.second, it.third) }
    val spentLeftToCategorize = transactionSet
        .map { it.map { it.uncategorizedAmounts }.sum() }
    val incomeLeftToCategorize = combineLatestAsTuple(accountsTotal, incomeCATotal, rowDatas, spentLeftToCategorize)
        .map { it.first - it.second - it.third.map { it.spent }.sum() - it.fourth }
    val uncategorizedBudgeted = combineLatestAsTuple(incomeLeftToCategorize, spentLeftToCategorize)
        .map { it.first + it.second }

    fun getRowDatas(transactionSet: List<Transaction>, activeCategories: List<Category>, incomeCA: SourceHashMap<Category, BigDecimal>): ArrayList<ReconcileRowData> {
        val rowDatas = ArrayList<ReconcileRowData>()
        for (category in activeCategories) {
            val spent = transactionSet.map { it.categoryAmounts[category.name] ?: BigDecimal.ZERO }.sum()
            rowDatas.add(ReconcileRowData(
                category,
                spent,
                incomeCA.itemObservables_.value[category] ?: error("it.third~[category] was null"))
            )
        }
        return rowDatas
    }

    fun getIncomeCA(activeCategories: List<Category>): SourceHashMap<Category, BigDecimal> {
        val newIncomeCA = SourceHashMap<Category, BigDecimal>()
        val oldIncomeCA = repo.readIncomeCA().associate { it.category to it.amount }
        for (category in activeCategories) {
            newIncomeCA[category] = oldIncomeCA[category.name] ?: BigDecimal.ZERO
        }
        return newIncomeCA
    }

    fun getActiveCategories(transactionSet: List<Transaction>): List<Category> {
        val activeCategories_ = HashSet<String>()
        for (transaction in transactionSet) {
            for (categoryAmount in transaction.categoryAmounts) {
                activeCategories_.add(categoryAmount.key)
            }
        }
        return activeCategories_.toList().map { categoriesVM.getCategoryByName(it) }
    }

    fun bindIncomeCAToRepo(incomeCA: SourceHashMap<Category, BigDecimal>) {
        incomeCA.observable // TODO("Handle disposables")
            .subscribe { repo.writeIncomeCA(it.map { IncomeCategoryAmounts(it.key.name, it.value) }) }
    }
}
