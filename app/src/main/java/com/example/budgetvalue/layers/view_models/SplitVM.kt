package com.example.budgetvalue.layers.view_models

import androidx.lifecycle.*
import com.example.budgetvalue.layers.data_layer.Repo
import com.example.budgetvalue.layers.z_ui.misc.SplitRowData
import com.example.budgetvalue.models.Account
import com.example.budgetvalue.models.Category
import com.example.budgetvalue.models.Transaction
import com.example.budgetvalue.util.*
import com.example.tmcommonkotlin.log
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
    val incomeCategoryAmounts = BehaviorSubject.createDefault(HashMap<Category, BigDecimal>())
    val rowDatas = combineLatestAsTuple(transactionSet, activeCategories, incomeCategoryAmounts).map {
        val rowDatas = ArrayList<SplitRowData>()
        for (category in it.second) {
            var spent = BigDecimal.ZERO
            for (transaction in it.first) {
                spent += transaction.categoryAmounts[category.name] ?: BigDecimal.ZERO
            }
            rowDatas.add(SplitRowData(
                category,
                spent,
                it.third[category] ?: BigDecimal.ZERO
            ))
        }
        rowDatas
    }.toBehaviorSubject()




    // spentCA depends on transactionSet + activeCategories
    val spentCategoryAmounts = combineLatestAsTuple(transactionSet, activeCategories)
        .map {
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
        spentCategoryAmounts_
    }.toBehaviorSubject()
    val budgetedCategoryAmounts = combineLatestAsTuple(
        activeCategories,
        spentCategoryAmounts,
        incomeCategoryAmounts
    ).map {
        val budgetedCategoryAmounts_ = HashMap<Category, BigDecimal>()
        for (category in it.first) {
            budgetedCategoryAmounts_[category] = (it.second[category]?: BigDecimal.ZERO) + (it.third[category]?: BigDecimal.ZERO)
        }
        budgetedCategoryAmounts_
    }.toBehaviorSubject()
}