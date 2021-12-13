package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.transactions.app.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

@HiltViewModel
class ReceiptCategorizationVM @Inject constructor(
) : ViewModel() {
    // # Setup
    val transaction = BehaviorSubject.create<Transaction>()

    // # Presentation State
    val description = transaction.map { it.description }
}