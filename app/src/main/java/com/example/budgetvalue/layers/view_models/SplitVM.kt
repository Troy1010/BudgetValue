package com.example.budgetvalue.layers.view_models

import androidx.lifecycle.*
import com.example.budgetvalue.layers.data_layer.Repo
import com.example.budgetvalue.models.Account
import com.example.budgetvalue.models.Category
import com.example.budgetvalue.models.Transaction
import com.example.budgetvalue.util.zipWithDefault
import java.math.BigDecimal

class SplitVM(private val repo: Repo, private val categoriesVM: CategoriesVM, private val transactionSet: LiveData<List<Transaction>>, private val accounts: LiveData<List<Account>>) : ViewModel() {
    val incomeTotal = MediatorLiveData<BigDecimal>()
    val activeCategories = MediatorLiveData<List<Category>>()
    val spentCategoryAmounts = MediatorLiveData<List<BigDecimal>>()
    val incomeCategoryAmounts = MediatorLiveData<List<String>>()
    val budgetedCategoryAmounts = MediatorLiveData<List<BigDecimal>>()
    init {
        // incomeTotal depends on accountsTotal and previousAccountsTotal, assumed to be 0 for now TODO
        incomeTotal.addSource(accounts) {
            incomeTotal.value = it.fold(BigDecimal.ZERO) {acc, account -> acc + account.amount }
        }
        // activeCategories depends on transactionSet, for now TODO
        activeCategories.addSource(transactionSet) {
            val activeCategories_ = HashSet<String>()
            for (transaction in it) {
                for (categoryAmount in transaction.categoryAmounts) {
                    activeCategories_.add(categoryAmount.key)
                }
            }
            activeCategories.value = activeCategories_.toList().map { categoriesVM.getCategoryByName(it) }
        }
        // spentCategoryAmounts depends on transactionSet and activeCategories
        spentCategoryAmounts.addSource(transactionSet) {
            val spentCategoryAmounts_ = HashMap<Category, BigDecimal>()
            val activeCategories_ = activeCategories.value ?: listOf()
            for (transaction in it) {
                for (category in activeCategories_) {
                    if (category !in spentCategoryAmounts_) {
                        spentCategoryAmounts_[category] = transaction.categoryAmounts[category.name]?:BigDecimal.ZERO
                    } else {
                        spentCategoryAmounts_[category] =
                            spentCategoryAmounts_[category]?.plus(transaction.categoryAmounts[category.name]?:BigDecimal.ZERO)?:BigDecimal.ZERO
                    }
                }
            }
            spentCategoryAmounts.value = spentCategoryAmounts_.map { it.value }
        }
        spentCategoryAmounts.addSource(activeCategories) {
            val spentCategoryAmounts_ = HashMap<Category, BigDecimal>()
            val transactions = transactionSet.value ?: listOf()
            for (transaction in transactions) {
                for (category in it) {
                    if (category !in spentCategoryAmounts_) {
                        spentCategoryAmounts_[category] = transaction.categoryAmounts[category.name]?:BigDecimal.ZERO
                    } else {
                        spentCategoryAmounts_[category] =
                            spentCategoryAmounts_[category]?.plus(transaction.categoryAmounts[category.name]?:BigDecimal.ZERO)?:BigDecimal.ZERO
                    }
                }
            }
            spentCategoryAmounts.value = spentCategoryAmounts_.map { it.value }
        }
        // budgetedCategoryAmounts depend on current Spent and Income, for now TODO
        budgetedCategoryAmounts.addSource(spentCategoryAmounts) {
            budgetedCategoryAmounts.value = it?.zipWithDefault(incomeCategoryAmounts.value?: listOf(), "0")
                ?.map { it.first + it.second.toBigDecimal() }
        }
        budgetedCategoryAmounts.addSource(incomeCategoryAmounts) {
            budgetedCategoryAmounts.value = spentCategoryAmounts.value?.zipWithDefault(it?: listOf(), "0")
                ?.map { it.first + it.second.toBigDecimal() }
        }
    }
}