package com.example.budgetvalue.layers.view_models

import androidx.lifecycle.*
import com.example.budgetvalue.layers.data_layer.Repo
import com.example.budgetvalue.layers.z_ui.misc.SplitRowData
import com.example.budgetvalue.layers.z_ui.misc.sum
import com.example.budgetvalue.models.Account
import com.example.budgetvalue.models.Category
import com.example.budgetvalue.models.IncomeCategoryAmounts
import com.example.budgetvalue.models.Transaction
import com.example.budgetvalue.util.*
import com.example.tmcommonkotlin.log
import com.example.tmcommonkotlin.logz
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashSet

class SplitVM(
    private val repo: Repo,
    private val categoriesVM: CategoriesVM,
    private val transactionSet: BehaviorSubject<List<Transaction>>,
    private val accounts: BehaviorSubject<List<Account>>
) : ViewModel() {
    val incomeTotal = accounts.map {
        it.fold(BigDecimal.ZERO) { acc, account -> acc + account.amount }
    }.toBehaviorSubject()
    val mainStream = transactionSet
        .map {
            // define activeCategories
            val activeCategories_ = HashSet<String>()
            for (transaction in it) {
                for (categoryAmount in transaction.categoryAmounts) {
                    activeCategories_.add(categoryAmount.key)
                }
            }
            activeCategories_.toList().map { categoriesVM.getCategoryByName(it) }
        }.map { activeCategories ->
            // define IncomeCA
            val newIncomeCA = SourceHashMap<Category, BigDecimal>()
            val oldIncomeCA = repo.readIncomeCA().associate { it.category to it.amount }
            for (category in activeCategories) {
                newIncomeCA[category] = oldIncomeCA[category] ?: BigDecimal.ZERO
            }
            Pair(activeCategories, newIncomeCA)
        }.doOnNext{
            // bind IncomeCA to database
            it.second.observable.subscribe({
                repo.writeIncomeCA(it.map { IncomeCategoryAmounts(it.key, it.value) })
            }) { logz("hhh:${it.narrate()}") }
        }.toBehaviorSubject()
    val activeCategories = mainStream
        .map {
            it.first
        }.toBehaviorSubject()
    val incomeCategoryAmounts = mainStream
        .map {
            it.second
        }.toBehaviorSubject()
    val incomeCATotal = incomeCategoryAmounts
        .map { it.map{ it.value }.sum() }.toBehaviorSubject()
    val rowDatas = zip(transactionSet, activeCategories, incomeCategoryAmounts)
        .map {
            // define rowDatas
            val rowDatas = ArrayList<SplitRowData>()
            for (category in it.second) {
                val spent = it.first.map { it.categoryAmounts[category.name] ?: BigDecimal.ZERO }.sum()
                rowDatas.add(SplitRowData(
                    category,
                    spent,
                    it.third.itemObservables_.value[category] ?: error("it.third~[category] was null"))
                )
            }
            rowDatas
        }.toBehaviorSubject()
    val spentLeftToCategorize = transactionSet
        .map {
            it.map { it.uncategorizedAmounts }.sum()
        }.toBehaviorSubject()
    val incomeLeftToCategorize = combineLatestAsTuple(incomeTotal, incomeCATotal, rowDatas, spentLeftToCategorize)
        .map {
            it.first - it.second - it.third.map { it.spent }.sum() - it.fourth
        }.toBehaviorSubject()
    val uncategorizedBudgeted = combineLatestAsTuple(incomeLeftToCategorize, spentLeftToCategorize)
        .map {
            it.first + it.second
        }
}
