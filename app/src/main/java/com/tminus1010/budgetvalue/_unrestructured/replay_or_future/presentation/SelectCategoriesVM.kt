package com.tminus1010.budgetvalue._unrestructured.replay_or_future.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem2
import com.tminus1010.budgetvalue._unrestructured.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.app.SelectCategoriesModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SelectCategoriesVM @Inject constructor(
    private val categoriesInteractor: CategoriesInteractor,
    private val selectCategoriesModel: SelectCategoriesModel,
) : ViewModel() {
    // # User Intents
    fun userSubmit() {
        navUp.easyEmit()
    }

    fun userToggleCategory(category: Category) {
        if (category in selectCategoriesModel.selectedCategories.value)
            selectCategoriesModel.unselectCategories(category)
        else
            selectCategoriesModel.selectCategories(category)
    }

    // # Events
    val navUp = MutableSharedFlow<Unit>()

    // # State
    val categoryButtonVMItems =
        categoriesInteractor.userCategories
            .map { categories ->
                categories.map { category ->
                    ButtonVMItem2(
                        title = category.name,
                        alpha = selectCategoriesModel.selectedCategories.map { if (category in it) 1F else 0.5F },
                        onClick = { userToggleCategory(category) }
                    )
                }
            }
    val buttons =
        flowOf(
            listOf(
                ButtonVMItem(
                    title = "Submit",
                    onClick = { userSubmit() }
                )
            )
        )
}