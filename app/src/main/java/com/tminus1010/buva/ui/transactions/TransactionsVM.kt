package com.tminus1010.buva.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.buva.all_layers.extensions.value
import com.tminus1010.buva.app.TransactionsInteractor
import com.tminus1010.buva.data.TransactionsRepo
import com.tminus1010.buva.framework.android.ShowAlertDialog
import com.tminus1010.buva.ui.all_features.ThrobberSharedVM
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.TableViewVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.TransactionPresentationModel
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsVM @Inject constructor(
    private val transactionsInteractor: TransactionsInteractor,
    private val transactionsRepo: TransactionsRepo,
    private val throbberSharedVM: ThrobberSharedVM,
) : ViewModel() {
    // # Setup
    val showAlertDialog = MutableSharedFlow<ShowAlertDialog>(1)

    // # User Intents
    fun userTryClearTransactionHistory() {
        GlobalScope.launch {
            showAlertDialog.value!!(
                body = NativeText.Simple("Are you sure you want to clear the transaction history?"),
                onYes = {
                    GlobalScope.launch(block = throbberSharedVM.decorate {
                        transactionsInteractor.clear()
                    })
                }
            )
        }
    }

    // # Internal
    private val transactionPresentationModels =
        transactionsRepo.transactionsAggregate
            .map { it.transactions.map(::TransactionPresentationModel) }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    // # Events
    val navToTransaction = transactionPresentationModels.flatMapLatest { merge(*it.map(TransactionPresentationModel::userTryNavToTransaction).toTypedArray()) }

    // # State
    val transactionVMItems =
        transactionPresentationModels
            .map {
                TableViewVMItem(
                    recipeGrid = it.map { it.toVMItems() },
                    shouldFitItemWidthsInsideTable = true,
                )
            }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val buttons =
        flowOf(
            listOf(
                ButtonVMItem(
                    title = "Clear",
                    onClick = ::userTryClearTransactionHistory
                )
            )
        )
}