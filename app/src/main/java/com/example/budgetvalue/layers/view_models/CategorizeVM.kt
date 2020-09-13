package com.example.budgetvalue.layers.view_models

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetvalue.layers.data_layer.Repo
import com.example.budgetvalue.models.Category
import com.example.budgetvalue.models.Transaction
import com.example.budgetvalue.util.toLiveData2
import com.example.tmcommonkotlin.logz
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal

class CategorizeVM(val repo: Repo, transactionsVM: TransactionsVM): ViewModel() {
    val transaction = MediatorLiveData<Transaction>()
    init {
        transaction.addSource(transactionsVM.uncategorizedSpends.toLiveData2()) {
            if (!it.isNullOrEmpty()) {
                viewModelScope.launch(Dispatchers.IO) {
                    transaction.postValue(it[0])
                }
            }
        }
    }
    fun setTransactionCategory(category: Category) {
        val transaction_ = transaction.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            transaction_.categoryAmounts.clear()
            transaction_.categoryAmounts[category.name] = transaction_.amount
            repo.updateTransaction(transaction_)
        }
    }
}