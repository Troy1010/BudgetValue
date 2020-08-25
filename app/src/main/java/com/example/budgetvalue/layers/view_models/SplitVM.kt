package com.example.budgetvalue.layers.view_models

import androidx.lifecycle.*
import com.example.budgetvalue.layers.data_layer.Repo
import com.example.budgetvalue.models.Account
import com.example.budgetvalue.models.Category
import com.example.budgetvalue.models.Transaction
import com.example.budgetvalue.observeOnce
import com.example.tmcommonkotlin.logz
import java.math.BigDecimal

class SplitVM(private val repo: Repo, private val transactionSet: LiveData<List<Transaction>>, private val accounts: LiveData<List<Account>>) : ViewModel() {
    val activeCategories = MutableLiveData<List<Category>>()
    val spentCategoryAmounts = MutableLiveData<List<String>>()
    val incomeTotal = MediatorLiveData<BigDecimal>()
    val incomeCategoryAmounts = MutableLiveData<List<String>>()
    val budgetedCategoryAmounts = MutableLiveData<List<String>>()
    init {
        // accountsTotal - previousAccountsTotal, assumed to be 0 for now TODO
        incomeTotal.addSource(accounts) {
            incomeTotal.value = it.fold(BigDecimal.ZERO) {acc, account -> acc + account.amount }
        }
        incomeTotal.observeOnce { logz("incomeTotal:${it}") }
    }
}