package com.tminus1010.budgetvalue._unrestructured.transactions.presentation

import com.tminus1010.budgetvalue._unrestructured.transactions.app.Transaction
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChooseTransactionSharedVM @Inject constructor() {
    // # User Intents
    val userSubmitTransaction = MutableSharedFlow<Transaction>()
    fun userSubmitTransaction(transaction: Transaction) {
        userSubmitTransaction.onNext(transaction)
    }
}