package com.tminus1010.budgetvalue._unrestructured.transactions.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._unrestructured.transactions.data.repo.TransactionsRepo
import com.tminus1010.budgetvalue._unrestructured.transactions.presentation.model.TransactionPresentationModel
import com.tminus1010.budgetvalue.all_layers.extensions.asObservable2
import com.tminus1010.budgetvalue.all_layers.extensions.value
import com.tminus1010.budgetvalue.framework.view.ShowAlertDialog
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsVM @Inject constructor(
    private val transactionsRepo: TransactionsRepo,
) : ViewModel() {
    // # Setup
    val showAlertDialog = MutableSharedFlow<ShowAlertDialog>(1)

    // # User Intents
    fun userTryClearTransactionHistory() {
        GlobalScope.launch {
            showAlertDialog.value!!(
                body = NativeText.Simple("Are you sure you want to clear the transaction history?"),
                onYes = { transactionsRepo.clear().subscribe() }
            )
        }
    }

    // # State
    val transactionVMItems =
        transactionsRepo.transactionsAggregate2
            .map { it.transactions.map(::TransactionPresentationModel) }
            .asObservable2()
            .replayNonError(1)
    val buttons = listOf(
        ButtonVMItem(
            title = "Clear",
            onClick = ::userTryClearTransactionHistory
        )
    )

    // # Events
    val navToTransaction = transactionVMItems.switchMap { Observable.merge(it.map(TransactionPresentationModel::userTryNavToTransaction)) }
}