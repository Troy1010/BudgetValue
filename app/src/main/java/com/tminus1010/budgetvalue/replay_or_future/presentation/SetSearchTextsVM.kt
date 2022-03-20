package com.tminus1010.budgetvalue.replay_or_future.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.all_features.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue.all_features.presentation.model.EditTextVMItem
import com.tminus1010.budgetvalue.all_features.presentation.model.MenuPresentationModel
import com.tminus1010.budgetvalue.all_features.presentation.model.MenuVMItem
import com.tminus1010.budgetvalue.budgeted.presentation.IHasToViewItemRecipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SetSearchTextsVM @Inject constructor(
    setSearchTextsSharedVM: SetSearchTextsSharedVM,
) : ViewModel() {
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
                                    )
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