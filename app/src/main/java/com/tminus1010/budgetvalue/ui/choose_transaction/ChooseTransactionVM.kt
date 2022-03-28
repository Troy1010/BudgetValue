package com.tminus1010.budgetvalue.ui.choose_transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue._unrestructured.transactions.data.repo.TransactionsRepo
import com.tminus1010.budgetvalue.ui.all_features.model.TextVMItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class ChooseTransactionVM @Inject constructor(
    chooseTransactionSharedVM: ChooseTransactionSharedVM,
    transactionsRepo: TransactionsRepo,
) : ViewModel() {
    // # Events
    val navUp = chooseTransactionSharedVM.userSubmitTransaction.map { Unit }

    // # State
    val isNoItemsMsgVisible =
        transactionsRepo.transactionsAggregate2
            .map { it.transactions.isEmpty() }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val recipeGrid =
        transactionsRepo.transactionsAggregate2
            .map { transactionsAggregate ->
                transactionsAggregate.transactions
                    .let { if (transactionsAggregate.mostRecentUncategorizedSpend == null) it else listOf(transactionsAggregate.mostRecentUncategorizedSpend!!) + it }
                    .distinctBy { it.description }
            }
            .map { it.map { listOf(TextVMItem(it.description, onClick = { chooseTransactionSharedVM.userSubmitTransaction(it) })) } }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
}