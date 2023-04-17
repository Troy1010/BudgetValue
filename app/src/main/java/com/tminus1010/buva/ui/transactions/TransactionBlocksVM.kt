package com.tminus1010.buva.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.buva.app.ReconciliationSkipInteractor
import com.tminus1010.buva.app.TransactionsInteractor
import com.tminus1010.buva.data.CurrentDatePeriod
import com.tminus1010.buva.data.SettingsRepo
import com.tminus1010.buva.domain.MiscUtil
import com.tminus1010.buva.ui.all_features.view_model_item.TableViewVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.TextPresentationModel
import com.tminus1010.buva.ui.all_features.view_model_item.TransactionBlockCompletionPresentationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class TransactionBlocksVM @Inject constructor(
    transactionsInteractor: TransactionsInteractor,
    private val currentDatePeriod: CurrentDatePeriod,
    private val reconciliationSkipInteractor: ReconciliationSkipInteractor,
    settingsRepo: SettingsRepo,
) : ViewModel() {
    // # State
    val transactionBlocksTableView =
        combine(transactionsInteractor.transactionBlocks, reconciliationSkipInteractor.reconciliationSkips, settingsRepo.anchorDateOffset)
        { transactionBlocks, reconciliationSkips, anchorDateOffset ->
            TableViewVMItem(
                recipeGrid = listOf(
                    listOf(
                        TextPresentationModel(text1 = "Transaction Block"),
                        TextPresentationModel(text1 = "Completion %"),
                    ),
                    *transactionBlocks.map { TransactionBlockCompletionPresentationModel(it, currentDatePeriod, MiscUtil.shouldSkip(reconciliationSkips, it, anchorDateOffset), reconciliationSkipInteractor).toPresentationModels() }.toTypedArray()
                ),
                shouldFitItemWidthsInsideTable = true,
            )
        }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val title = flowOf("Transaction Blocks %s")
}