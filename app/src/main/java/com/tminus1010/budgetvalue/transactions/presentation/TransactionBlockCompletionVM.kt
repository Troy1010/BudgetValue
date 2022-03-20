package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.all_features.data.repo.CurrentDatePeriodRepo
import com.tminus1010.budgetvalue.all_features.presentation.model.TextPresentationModel
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.budgetvalue.transactions.presentation.model.TransactionBlockCompletionPresentationModel
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
                        TextPresentationModel(text1 = "Transaction Block"),
                        TextPresentationModel(text1 = "Completion %"),
                    ),
                    *it.map { TransactionBlockCompletionPresentationModel(it, currentDatePeriodRepo).toPresentationModels() }.toTypedArray()
                )
            }
            .replayNonError(1)
    val title =
        flow { emit("Transaction Block Completion %s") }
}