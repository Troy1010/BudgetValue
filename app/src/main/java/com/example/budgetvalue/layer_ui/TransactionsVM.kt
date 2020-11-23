package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetvalue.layer_data.Repo
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream

class TransactionsVM(private val repo: Repo):ViewModel() {
    val transactions = repo.getTransactions()
        .toBehaviorSubject()
    val spends = transactions
        .map { it.filter { it.isSpend } }
    val uncategorizedSpends = spends
        .map { it.filter { it.isUncategorized } }
    val uncategorizedSpendsSize = uncategorizedSpends
        .map { it.size.toString() }
    fun importTransactions(inputStream: InputStream) {
        Completable
            .fromAction {
                val transactions = repo.parseToTransactions(inputStream)
                repo.clearTransactions()
                repo.add(transactions)
            }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
}