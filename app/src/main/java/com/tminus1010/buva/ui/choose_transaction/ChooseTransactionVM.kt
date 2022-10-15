package com.tminus1010.buva.ui.choose_transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.buva.app.TransactionsInteractor
import com.tminus1010.buva.data.TransactionsRepo
import com.tminus1010.buva.ui.all_features.view_model_item.TextVMItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class ChooseTransactionVM @Inject constructor(
    chooseTransactionSharedVM: ChooseTransactionSharedVM,
    transactionsInteractor: TransactionsInteractor,
) : ViewModel() {
    // # Events
    val navUp = chooseTransactionSharedVM.userSubmitTransaction.map { Unit }

    // # State
    val isNoItemsMsgVisible =
        transactionsInteractor.transactionsAggregate
            .map { it.transactions.isEmpty() }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val recipeGrid =
        transactionsInteractor.transactionsAggregate
            .map { transactionsAggregate ->
                transactionsAggregate.transactions
                    .let { if (transactionsAggregate.mostRecentUncategorizedSpend == null) it else listOf(transactionsAggregate.mostRecentUncategorizedSpend!!) + it }
                    .distinctBy { it.description }
            }
            .map { it.map { listOf(TextVMItem(it.description, onClick = { chooseTransactionSharedVM.userSubmitTransaction(it) })) } }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
}