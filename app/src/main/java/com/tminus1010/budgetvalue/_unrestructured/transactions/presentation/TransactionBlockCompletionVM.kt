package com.tminus1010.budgetvalue._unrestructured.transactions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue._unrestructured.transactions.presentation.model.TransactionBlockCompletionPresentationModel
import com.tminus1010.budgetvalue.app.TransactionsInteractor
import com.tminus1010.budgetvalue.data.CurrentDatePeriodRepo
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.TextPresentationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class TransactionBlockCompletionVM @Inject constructor(
    private val transactionsInteractor: TransactionsInteractor,
    private val currentDatePeriodRepo: CurrentDatePeriodRepo,
) : ViewModel() {
    // # State
    val transactionVMItems =
        transactionsInteractor.transactionBlocks2
            .map {
                listOf(
                    listOf(
                        TextPresentationModel(text1 = "Transaction Block"),
                        TextPresentationModel(text1 = "Completion %"),
                    ),
                    *it.map { TransactionBlockCompletionPresentationModel(it, currentDatePeriodRepo).toPresentationModels() }.toTypedArray()
                )
            }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val title =
        flow { emit("Transaction Block Completion %s") }
}