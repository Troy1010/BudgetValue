package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.middleware.presentation.ButtonVMItem
import com.tminus1010.budgetvalue._core.presentation_and_view._view_model_items.PopupVMItem
import com.tminus1010.budgetvalue._middleware.framework.createPublishSubject
import com.tminus1010.budgetvalue.all.framework.extensions.onNext2
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
    // # User Intents
    val userTryClearTransactionHistory = createPublishSubject()

    // # Presentation State
    val transactionVMItems =
        transactionsRepo.transactions2
            .map { it.transactions.map(::TransactionVMItem) }
            .replayNonError(1)
    val buttons = listOf(
        ButtonVMItem(
            title = "Clear",
            userClick = userTryClearTransactionHistory::onNext2
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