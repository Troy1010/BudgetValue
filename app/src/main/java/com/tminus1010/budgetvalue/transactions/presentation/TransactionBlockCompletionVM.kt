package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.data.repo.CurrentDatePeriodRepo
import com.tminus1010.budgetvalue.history.presentation.TextPresentationModel
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.budgetvalue.transactions.presentation.model.TransactionBlockCompletionVMItem
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class TransactionBlockCompletionVM @Inject constructor(
    private val transactionsInteractor: TransactionsInteractor,
    private val currentDatePeriodRepo: CurrentDatePeriodRepo,
) : ViewModel() {
    // # State
    val transactionVMItems =
        transactionsInteractor.transactionBlocks
            .map {
                listOf(
                    listOf(
                        TextPresentationModel("Transaction Block"),
                        TextPresentationModel("Completion %"),
                    ),
                    *it.map { TransactionBlockCompletionVMItem(it, currentDatePeriodRepo).toPresentationModels() }.toTypedArray()
                )
            }
            .replayNonError(1)
    val title =
        flow { emit("Transaction Block Completion %s") }
}