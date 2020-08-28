package com.example.budgetvalue.layers.view_models

import androidx.lifecycle.*
import com.example.budgetvalue.layers.data_layer.Repo
import com.example.budgetvalue.models.Account
import com.example.budgetvalue.models.Category
import com.example.budgetvalue.models.Transaction
import com.example.budgetvalue.util.toBehaviorSubject
import com.example.budgetvalue.util.toLiveData2
import com.example.budgetvalue.util.zipWithDefault
import com.example.tmcommonkotlin.logz
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
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
    val activeCategories = transactionSet.map {
        val activeCategories_ = HashSet<String>()
        for (transaction in it) {
            for (categoryAmount in transaction.categoryAmounts) {
                activeCategories_.add(categoryAmount.key)
            }
        }
        activeCategories_.toList().map { categoriesVM.getCategoryByName(it) }
    }.toBehaviorSubject()

    // spentCA depends on transactionSet + activeCategories
    val spentCategoryAmounts = Observable.combineLatest(listOf(transactionSet, activeCategories)) {
        Pair(it[0] as List<Transaction>, it[1] as List<Category>)
    }.map {
        val spentCategoryAmounts_ = HashMap<Category, BigDecimal>()
        for (transaction in it.first) {
            for (category in it.second) {
                if (category !in spentCategoryAmounts_) {
                    spentCategoryAmounts_[category] =
                        transaction.categoryAmounts[category.name] ?: BigDecimal.ZERO
                } else {
                    spentCategoryAmounts_[category] =
                        spentCategoryAmounts_[category]?.plus(
                            transaction.categoryAmounts[category.name] ?: BigDecimal.ZERO
                        ) ?: BigDecimal.ZERO
                }
            }
        }
        spentCategoryAmounts_.map { it.value }
    }.toBehaviorSubject()
    val incomeCategoryAmounts = BehaviorSubject.createDefault(listOf<BigDecimal>())
    val budgetedCategoryAmounts = Observable.combineLatest(listOf(spentCategoryAmounts, incomeCategoryAmounts)) {
        Pair(it[0] as List<BigDecimal>, it[1] as List<BigDecimal>)
    }.map {
        it.first.zipWithDefault(it.second, BigDecimal.ZERO)
            .map { it.first + it.second }
    }.toBehaviorSubject()
}