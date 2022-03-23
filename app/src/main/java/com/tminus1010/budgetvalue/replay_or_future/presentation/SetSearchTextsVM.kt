package com.tminus1010.budgetvalue.replay_or_future.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.all_features.ui.all_features.model.ButtonVMItem
import com.tminus1010.budgetvalue.all_features.ui.all_features.model.EditTextVMItem
import com.tminus1010.budgetvalue.all_features.ui.all_features.model.MenuPresentationModel
import com.tminus1010.budgetvalue.all_features.ui.all_features.model.MenuVMItem
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SetSearchTextsVM @Inject constructor(
    setSearchTextsSharedVM: SetSearchTextsSharedVM,
) : ViewModel() {
    // # Events
    val navToCopyFromTransactions = MutableSharedFlow<Unit>()

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
                                        onClick = { navToCopyFromTransactions.onNext() }
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