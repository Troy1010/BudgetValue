package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue._core.presentation.model.PopupVMItem
import com.tminus1010.budgetvalue.transactions.data.repo.TransactionsRepo
import com.tminus1010.budgetvalue.transactions.presentation.model.TransactionVMItem
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class TransactionsVM @Inject constructor(
    transactionsRepo: TransactionsRepo,
) : ViewModel() {
    // # User Intents
    val userTryClearTransactionHistory = PublishSubject.create<Unit>()

    // # State
    val transactionVMItems =
        transactionsRepo.transactionsAggregate
            .map { it.transactions.map(::TransactionVMItem) }
            .replayNonError(1)
    val buttons = listOf(
        ButtonVMItem(
            title = "Clear",
            onClick = { userTryClearTransactionHistory.onNext(Unit) }
        )
    )

    // # Events
    val navToTransaction = transactionVMItems.switchMap { Observable.merge(it.map(TransactionVMItem::userTryNavToTransaction)) }
    val alertDialog = userTryClearTransactionHistory.map {
        PopupVMItem(
            msg = "Are you sure you want to clear the transaction history?",
            onYes = { transactionsRepo.clear().subscribe() }
        )
    }
}