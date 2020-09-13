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
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap
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
    val activeCategories = transactionSet
        .map {
            val activeCategories_ = HashSet<String>()
            for (transaction in it) {
                for (categoryAmount in transaction.categoryAmounts) {
                    activeCategories_.add(categoryAmount.key)
                }
            }
            activeCategories_.toList().map { categoriesVM.getCategoryByName(it) }
        }
        .doOnNext {
            // bind to IncomeCategoryAmounts db
            runBlocking {
                for (category in it) {
                    if (!repo.doesIncomeCategoryAmountHaveCategory(category)) {
                        repo.addIncomeCategoryAmount(category)
                    }
                }
                // should remove anything from ICA that is not in activeCategories
            }
        }
        .toBehaviorSubject()
    val categorizedIncomesChanged = PublishSubject.create<BigDecimal>()
    val categorizedIncomesTotal = categorizedIncomesChanged
        .scan(BigDecimal.ZERO, BigDecimal::add)
    var iSkipCount = 0
    val incomeCategoryAmounts = combineLatestAsTuple(repo.getIncomeCategoryAmounts(), activeCategories)
        .filter {
            if (iSkipCount>0) {
                iSkipCount--
                false
            } else {
                true
            }
        }
        .map {
            val incomeCA = it.first.associate { ca -> Pair(ca.category, ca.amount) }
            val activeCategories = it.second
            val returning = HashMap<Category, BehaviorSubject<BigDecimal>>()
            for (category in activeCategories) {
                val initValue = incomeCA[category] ?: BigDecimal.ZERO
                val bs = BehaviorSubject.createDefault(initValue)
                returning[category] = bs
            }
            returning
        }
        .doOnNext {
            for (pair in it) {
                pair.value.skip(1).subscribe {
                    // bind BehaviorSubjects->db
                    viewModelScope.launch {
                        iSkipCount++ // TODO: This is pretty hacky
                        repo.updateIncomeCategoryAmount(IncomeCategoryAmounts(pair.key, it))
                    }
                }
                pair.value.pairwiseDefault(BigDecimal.ZERO).map { it.second - it.first }.subscribe(categorizedIncomesChanged) // TODO: This is pretty hacky
            }
        }
    val rowDatas = combineLatestAsTuple(transactionSet, zip(activeCategories, incomeCategoryAmounts))
        .map { Triple(it.first, it.second.first, it.second.second) }
        .map {
            val rowDatas = ArrayList<SplitRowData>()
            for (category in it.second) {
                val spent = it.first.map { it.categoryAmounts[category.name] ?: BigDecimal.ZERO }.sum()
                rowDatas.add(SplitRowData(
                    category,
                    spent,
                    it.third[category] ?: error("it.third[category] was null")
                ))
            }
            rowDatas
        }.toBehaviorSubject()
    val spentLeftToCategorize = transactionSet
        .map {
            it.map { it.uncategorizedAmounts }.sum()
        }.toBehaviorSubject()
    val incomeLeftToCategorize = combineLatestAsTuple(incomeTotal, categorizedIncomesTotal)
        .map {
            it.first - it.second
        }.toBehaviorSubject()
    val uncategorizedBudgeted = combineLatestAsTuple(incomeLeftToCategorize, spentLeftToCategorize)
        .map {
            it.first + it.second
        }
}
