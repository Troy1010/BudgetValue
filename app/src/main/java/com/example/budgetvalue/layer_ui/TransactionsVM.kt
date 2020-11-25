package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.layer_data.Repo
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.InputStream

class TransactionsVM(private val repo: Repo):ViewModel() {
    val transactions = repo.transactions
    val spends = transactions
        .map { it.filter { it.isSpend } }
    val uncategorizedSpends = spends
        .map { it.filter { it.isUncategorized } }
    val uncategorizedSpendsSize = uncategorizedSpends
        .map { it.size.toString() }
    fun importTransactions(inputStream: InputStream) {
        repo.clearTransactions()
            .andThen(repo.add(repo.parseToTransactions(inputStream)))
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
}