package com.example.budgetvalue.layers.view_models

import androidx.lifecycle.*
import com.example.budgetvalue.layers.data_layer.Repo
import com.example.budgetvalue.models.Account
import com.example.budgetvalue.models.Category
import com.example.budgetvalue.models.Transaction
import com.example.tmcommonkotlin.logz
import java.math.BigDecimal

class SplitVM(private val repo: Repo, private val categoriesVM: CategoriesVM, private val transactionSet: LiveData<List<Transaction>>, private val accounts: LiveData<List<Account>>) : ViewModel() {
    val incomeTotal = MediatorLiveData<BigDecimal>()
    val activeCategories = MediatorLiveData<List<Category>>()
    val spentCategoryAmounts = MediatorLiveData<List<String>>()
    val incomeCategoryAmounts = MediatorLiveData<List<String>>()
    val budgetedCategoryAmounts = MediatorLiveData<List<String>>()
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
    }
}