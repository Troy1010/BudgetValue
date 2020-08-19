package com.example.budgetvalue.layers.view_models

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetvalue.layers.data_layer.Repo
import com.example.budgetvalue.models.Account
import com.example.budgetvalue.models.Transaction
import com.example.tmcommonkotlin.logz
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream

class TransactionsVM(private val repo: Repo):ViewModel() {
    val transactions = repo.getTransactions()
    val uncategorizedTransactions = MediatorLiveData<List<Transaction>>()
    fun importTransactions(inputStream: InputStream) {
        viewModelScope.launch(Dispatchers.IO) {
            val transactions = repo.parseInputStreamToTransactions(inputStream)
            repo.clear()
            repo.addTransaction(transactions)
        }
    }
    init {
        uncategorizedTransactions.addSource(transactions) {
            uncategorizedTransactions.value = it.filter { it.isUncategorized }
        }
    }
}