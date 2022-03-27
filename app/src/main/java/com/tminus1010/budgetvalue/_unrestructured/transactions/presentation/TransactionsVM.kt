package com.tminus1010.budgetvalue._unrestructured.transactions.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._unrestructured.transactions.data.repo.TransactionsRepo
import com.tminus1010.budgetvalue._unrestructured.transactions.presentation.model.TransactionPresentationModel
import com.tminus1010.budgetvalue.all_layers.extensions.asObservable2
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem
import com.tminus1010.budgetvalue.ui.all_features.model.PopupVMItem
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TransactionsVM @Inject constructor(
    transactionsRepo: TransactionsRepo,
) : ViewModel() {
    // # User Intents
    val userTryClearTransactionHistory = PublishSubject.create<Unit>()

    // # State
    val transactionVMItems =
        transactionsRepo.transactionsAggregate2
            .map { it.transactions.map(::TransactionPresentationModel) }
            .asObservable2()
            .replayNonError(1)
    val buttons = listOf(
        ButtonVMItem(
            title = "Clear",
            onClick = { userTryClearTransactionHistory.onNext(Unit) }
        )
    )

    // # Events
    val navToTransaction = transactionVMItems.switchMap { Observable.merge(it.map(TransactionPresentationModel::userTryNavToTransaction)) }
    val alertDialog = userTryClearTransactionHistory.map {
        PopupVMItem(
            msg = "Are you sure you want to clear the transaction history?",
            onYes = { transactionsRepo.clear().subscribe() }
        )
    }
}