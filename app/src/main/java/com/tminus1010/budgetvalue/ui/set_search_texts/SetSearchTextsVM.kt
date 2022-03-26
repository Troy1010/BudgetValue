package com.tminus1010.budgetvalue.ui.set_search_texts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue._unrestructured.transactions.presentation.ChooseTransactionSharedVM
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.all_layers.extensions.value
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem
import com.tminus1010.budgetvalue.ui.all_features.model.EditTextVMItem
import com.tminus1010.budgetvalue.ui.all_features.model.MenuVMItems
import com.tminus1010.budgetvalue.ui.all_features.model.MenuVMItem
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
        chooseTransactionSharedVM.userSubmitTransaction.observe(viewModelScope) { setSearchTextsSharedVM.searchTexts[lastChosenIndex.value!!] = it.description }
    }

    // # State
    val recipeGrid =
        setSearchTextsSharedVM.searchTexts.flow
            .map { sourceList ->
                listOf(
                    *sourceList.withIndex().map { (i, s) ->
                        listOf<IHasToViewItemRecipe>(
                            EditTextVMItem(
                                text = s,
                                onDone = { sourceList[i] = it },
                                menuPresentationModel = MenuVMItems(
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