package com.tminus1010.buva.ui.set_search_texts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.all_layers.extensions.value
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.EditTextVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.MenuVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.MenuVMItems
import com.tminus1010.buva.ui.choose_transaction.ChooseTransactionSharedVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.customviews.IHasToViewItemRecipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
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
                                menuVMItems = MenuVMItems(
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