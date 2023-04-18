package com.tminus1010.buva.ui.all_features.view_model_item

import androidx.lifecycle.asLiveData
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipeFactory
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.AmountFormula
import kotlinx.coroutines.flow.StateFlow

// TODO
data class CategoryAmountFormulaPresentationModel(
    private val category: Category,
    private val fillCategory: Category?,
    private val amountFormula: StateFlow<AmountFormula>,
    private val userSetFillCategory: (Category) -> Unit,
    private val onNewAmountFormula: (AmountFormula) -> Unit,
) {
    fun toHasToViewItemRecipes(): List<ViewItemRecipeFactory> {
        return listOf(
            TextVMItem(category.name),
            if (fillCategory == category) AmountFormulaPresentationModel2(amountFormula) else AmountFormulaPresentationModel1(amountFormula.asLiveData(), onNewAmountFormula),
            CheckboxVMItem2(initialValue = fillCategory == category, onChecked = { userSetFillCategory(category) }),
        )
    }
}

