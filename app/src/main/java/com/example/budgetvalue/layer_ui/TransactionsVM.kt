package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetvalue.layer_data.Repo
import com.example.budgetvalue.toBehaviorSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream

class TransactionsVM(private val repo: Repo):ViewModel() {
    val transactions = repo.getTransactions().toBehaviorSubject()
    val spends = transactions
        .map { it.filter { it.isSpend } }.toBehaviorSubject()
    val uncategorizedSpends = spends
        .map { it.filter { it.isUncategorized } }
    val uncategorizedSpendsSize = uncategorizedSpends
        .map { it.size.toString() }
    fun importTransactions(inputStream: InputStream) {
        viewModelScope.launch(Dispatchers.IO) {
            val transactions = repo.parseInputStreamToTransactions(inputStream)
            repo.clear()
            repo.addTransaction(transactions)
        }
    }
}