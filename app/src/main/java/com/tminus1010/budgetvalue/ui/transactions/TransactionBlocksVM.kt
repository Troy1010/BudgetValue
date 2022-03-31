package com.tminus1010.budgetvalue.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue.app.TransactionsInteractor
import com.tminus1010.budgetvalue.data.CurrentDatePeriod
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.TableViewVMItem
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.TextPresentationModel
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.TransactionBlockCompletionPresentationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class TransactionBlocksVM @Inject constructor(
    private val transactionsInteractor: TransactionsInteractor,
    private val currentDatePeriod: CurrentDatePeriod,
) : ViewModel() {
    // # State
    val transactionBlocksTableView =
        transactionsInteractor.transactionBlocks
            .map {
                TableViewVMItem(
                    recipeGrid = listOf(
                        listOf(
                            TextPresentationModel(text1 = "Transaction Block"),
                            TextPresentationModel(text1 = "Completion %"),
                        ),
                        *it.map { TransactionBlockCompletionPresentationModel(it, currentDatePeriod).toPresentationModels() }.toTypedArray()
                    ),
                    shouldFitItemWidthsInsideTable = true,
                )
            }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val title = flowOf("Transaction Blocks %s")
}