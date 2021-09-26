package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.transactions.domain.TransactionsAppService
import com.tminus1010.budgetvalue.transactions.models.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class TransactionsVM @Inject constructor(
    transactionsAppService: TransactionsAppService,
) : ViewModel() {
    // # Input
    fun userTryNavToTransaction(transaction: Transaction) {
        navToTransation.onNext(transaction)
    }

    // # Output
    val transactions = transactionsAppService.transactions
    val navToTransation = PublishSubject.create<Transaction>()!!
}