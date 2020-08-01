package com.example.budgetvalue.layers.view_models

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetvalue.layers.data_layer.Repo
import com.example.tmcommonkotlin.logz
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.lang.Exception

class TransactionsVM(private val repo: Repo):ViewModel() {
    val transactions = repo.getTransactions()
    fun importTransactions(inputStream: InputStream) {
        viewModelScope.launch(Dispatchers.IO) {
            val transactions = repo.parseInputStreamToTransactions(inputStream)
            repo.clear()
            repo.add(transactions)
            logz("transactions added.")
        }
    }
    init {
        transactions.observeForever {
            logz("new transactions:${it.joinToString(",")}")
        }
    }
}