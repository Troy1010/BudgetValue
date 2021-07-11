package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.transactions.models.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionVM @Inject constructor() : ViewModel() {
    // # Input
    fun setup(_transaction: Transaction) {
        transaction = _transaction
    }

    // # Output
    lateinit var transaction: Transaction
}