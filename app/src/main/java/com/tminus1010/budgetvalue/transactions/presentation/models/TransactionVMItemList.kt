package com.tminus1010.budgetvalue.transactions.presentation.models

import com.tminus1010.budgetvalue.transactions.models.Transaction
import io.reactivex.rxjava3.core.Observable

class TransactionVMItemList(private val transactions: List<Transaction>) : List<TransactionVMItem> by transactions.map(::TransactionVMItem) {
    val navToTransaction = Observable.merge(this.map { it.userTryNavToTransaction })
}