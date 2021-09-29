package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.transactions.domain.TransactionsAppService
import com.tminus1010.budgetvalue.transactions.presentation.models.TransactionVMItemList
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionsVM @Inject constructor(
    transactionsAppService: TransactionsAppService,
) : ViewModel() {
    // # Presentation State
    val transactions = transactionsAppService.transactions2
        .map(::TransactionVMItemList)
        .replayNonError(1)

    // # Events
    val navToTransaction = transactions.switchMap { it.navToTransaction }
}