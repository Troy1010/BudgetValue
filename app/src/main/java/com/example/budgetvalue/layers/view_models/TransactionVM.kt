package com.example.budgetvalue.layers.view_models

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetvalue.layers.data_layer.Repo
import com.example.budgetvalue.models.Category
import com.example.budgetvalue.models.Transaction
import com.example.tmcommonkotlin.logz
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal

class TransactionVM(val repo: Repo, transactionsVM: TransactionsVM): ViewModel() {
    val transaction = MediatorLiveData<Transaction>()
    init {
        transaction.addSource(transactionsVM.transactions) {
            if (it!=null) {
                viewModelScope.launch(Dispatchers.IO) {
                    transaction.postValue(it[0])
                }
                transaction.removeSource(transactionsVM.transactions)
            }
        }
    }
    fun setTransactionCategory(category: Category) {
        val transaction = transaction.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val newCategoryAmounts = HashMap<String, BigDecimal>()
            newCategoryAmounts[category.name] = transaction.amount.toBigDecimal()
            transaction.categoryAmounts = Gson().toJson(newCategoryAmounts)
            repo.updateTransaction(transaction)
        }
    }
}