package com.tminus1010.budgetvalue.replay_or_future.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.value
import com.tminus1010.budgetvalue.all_features.ui.all_features.model.ButtonVMItem
import com.tminus1010.budgetvalue.all_features.ui.all_features.model.EditTextVMItem
import com.tminus1010.budgetvalue.all_features.ui.all_features.model.MenuPresentationModel
import com.tminus1010.budgetvalue.all_features.ui.all_features.model.MenuVMItem
import com.tminus1010.budgetvalue.transactions.presentation.ChooseTransactionSharedVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SetSearchTextsVM @Inject constructor(
    setSearchTextsSharedVM: SetSearchTextsSharedVM,
    chooseTransactionSharedVM: ChooseTransactionSharedVM,
) : ViewModel() {
    // # Events
    val navToChooseTransaction = MutableSharedFlow<Int>()

    // # Internal
    private val lastChosenIndex = navToChooseTransaction.shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    init {
        chooseTransactionSharedVM.userSubmitDescription.observe(viewModelScope) { setSearchTextsSharedVM.searchTexts.value[lastChosenIndex.value!!] = it }
    }

    // # State
    val recipeGrid =
        setSearchTextsSharedVM.searchTexts.flatMapLatest { it.flow }
            .map { sourceList ->
                listOf(
                    *sourceList.withIndex().map { (i, s) ->
                        listOf<IHasToViewItemRecipe>(
                            EditTextVMItem(
                                text = s,
                                onDone = { sourceList[i] = it },
                                menuPresentationModel = MenuPresentationModel(
                                    MenuVMItem(
                                        title = "Delete",
                                        onClick = { sourceList.removeAt(i) }
                                    ),
                                    MenuVMItem(
                                        title = "Copy from Transactions",
                                        onClick = { navToChooseTransaction.onNext(i) }
                                    ),
                                )
                            )
                        )
                    }.toTypedArray(),
                    listOf<IHasToViewItemRecipe>(
                        ButtonVMItem(
                            title = "Add Another Search Text",
                            onClick = { sourceList.add("") },
                        ),
                    ),
                )
            }
}