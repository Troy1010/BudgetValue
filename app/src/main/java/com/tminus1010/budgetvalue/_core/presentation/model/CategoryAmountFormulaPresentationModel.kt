package com.tminus1010.budgetvalue._core.presentation.model

import com.tminus1010.budgetvalue.budgeted.presentation.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.categories.models.Category

// TODO
data class CategoryAmountFormulaPresentationModel(
    private val category: Category,
) {
    fun toHasToViewItemRecipes(): List<IHasToViewItemRecipe> {
        return listOf(
            TextVMItem(category.name),
            TextVMItem(category.name),
            TextVMItem(category.name),
        )
    }
}

