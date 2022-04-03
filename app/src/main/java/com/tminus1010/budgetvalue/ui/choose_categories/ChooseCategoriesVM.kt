package com.tminus1010.budgetvalue.ui.choose_categories

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.ButtonVMItem2
import com.tminus1010.budgetvalue.app.CategoryAdapter
import com.tminus1010.budgetvalue.app.UserCategories
import com.tminus1010.budgetvalue.domain.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ChooseCategoriesVM @Inject constructor(
    private val userCategories: UserCategories,
    private val chooseCategoriesSharedVM: ChooseCategoriesSharedVM,
) : ViewModel() {
    // # User Intents
    fun userSubmit() {
        navUp.easyEmit()
    }

    fun userToggleCategory(category: Category) {
        if (category in chooseCategoriesSharedVM.selectedCategories.value)
            chooseCategoriesSharedVM.unselectCategories(category)
        else
            chooseCategoriesSharedVM.selectCategories(category)
    }

    // # Events
    val navUp = MutableSharedFlow<Unit>()

    // # State
    val categoryButtonVMItems =
        userCategories.flow
            .map { categories ->
                categories.map { category ->
                    ButtonVMItem2(
                        title = category.name,
                        alpha = chooseCategoriesSharedVM.selectedCategories.map { if (category in it) 1F else 0.5F },
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