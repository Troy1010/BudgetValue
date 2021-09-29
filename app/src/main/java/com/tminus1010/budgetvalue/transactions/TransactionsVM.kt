package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.transactions.data.TransactionsRepo
import com.tminus1010.budgetvalue.transactions.presentation.models.TransactionVMItem
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

@HiltViewModel
class TransactionsVM @Inject constructor(
    transactionsRepo: TransactionsRepo,
) : ViewModel() {
    // # Presentation State
    val transactionVMItems =
        transactionsRepo.transactions2
            .map { it.transactions.map(::TransactionVMItem) }
            .replayNonError(1)

    // # Events
    val navToTransaction = transactionVMItems.switchMap { Observable.merge(it.map(TransactionVMItem::userTryNavToTransaction)) }
}