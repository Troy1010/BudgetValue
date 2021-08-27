package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.budgetvalue.transactions.models.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class TransactionsVM @Inject constructor(
    transactionsDomain: TransactionsDomain,
) : ViewModel() {
    // # Input
    fun userTryNavToTransaction(transaction: Transaction) {
        navToTransation.onNext(transaction)
    }

    // # Output
    val transactions = transactionsDomain.transactions
    val navToTransation = PublishSubject.create<Transaction>()!!
}