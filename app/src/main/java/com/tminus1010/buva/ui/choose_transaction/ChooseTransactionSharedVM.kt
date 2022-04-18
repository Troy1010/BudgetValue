package com.tminus1010.buva.ui.choose_transaction

import com.tminus1010.buva.domain.Transaction
import com.tminus1010.buva.all_layers.extensions.onNext
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