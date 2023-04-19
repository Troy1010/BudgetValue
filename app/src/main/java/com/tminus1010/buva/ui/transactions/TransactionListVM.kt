package com.tminus1010.buva.ui.transactions

import android.view.View
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.app.TransactionsInteractor
import com.tminus1010.buva.environment.android_wrapper.ActivityWrapper
import com.tminus1010.buva.environment.android_wrapper.ParcelableTransactionToBooleanLambdaWrapper
import com.tminus1010.buva.ui.all_features.ThrobberSharedVM
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.TableViewVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.TransactionPresentationModel
import com.tminus1010.tmcommonkotlin.coroutines.extensions.use
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionListVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val transactionsInteractor: TransactionsInteractor,
    private val throbberSharedVM: ThrobberSharedVM,
    private val activityWrapper: ActivityWrapper,
) : ViewModel() {
    // # User Intents
    fun userTryClearTransactionHistory() {
        GlobalScope.launch {
            activityWrapper.showAlertDialog(
                body = NativeText.Simple("Are you sure you want to clear the transaction history?"),
                onYes = {
                    GlobalScope.launch { transactionsInteractor.clear() }
                        .use(throbberSharedVM)
                }
            )
        }
    }

    // # Private
    private val transactionFilter = savedStateHandle.get<ParcelableTransactionToBooleanLambdaWrapper>(KEY1)
    private val transactionPresentationModels =
        transactionsInteractor.transactionsAggregate
            .map { it.transactions.filter { transactionFilter?.lambda?.invoke(it) ?: true }.map(::TransactionPresentationModel) }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    // # Events
    val navToTransaction = transactionPresentationModels.flatMapLatest { merge(*it.map(TransactionPresentationModel::userTryNavToTransaction).toTypedArray()) }

    // # State
    val noTransactionsMsgVisibility = transactionPresentationModels.map { if (it.isEmpty()) View.VISIBLE else View.GONE }
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