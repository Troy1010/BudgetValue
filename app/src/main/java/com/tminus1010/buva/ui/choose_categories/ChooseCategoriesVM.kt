package com.tminus1010.buva.ui.choose_categories

import androidx.lifecycle.ViewModel
import com.tminus1010.buva.app.UserCategories
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.ui.all_features.Navigator
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ChooseCategoriesVM @Inject constructor(
    private val userCategories: UserCategories,
    private val chooseCategoriesSharedVM: ChooseCategoriesSharedVM,
    private val navigator: Navigator,
) : ViewModel() {
    // # User Intents
    fun userToggleCategory(category: Category) {
        if (category in chooseCategoriesSharedVM.selectedCategories.value)
            chooseCategoriesSharedVM.unselectCategories(category)
        else
            chooseCategoriesSharedVM.selectCategories(category)
    }

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
                    onClick = { navigator.navUp() }
                )
            )
        )
}