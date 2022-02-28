package com.tminus1010.budgetvalue._core.presentation.model

import com.tminus1010.budgetvalue.budgeted.presentation.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import kotlinx.coroutines.flow.StateFlow

// TODO
data class CategoryAmountFormulaPresentationModel(
    private val category: Category,
    private val fillCategory: Category?,
    private val amountFormula: StateFlow<AmountFormula>,
    private val userSetFillCategory: (Category) -> Unit,
    private val onNewAmountFormula: (AmountFormula) -> Unit,
) {
    fun toHasToViewItemRecipes(): List<IHasToViewItemRecipe> {
        return listOf(
            TextVMItem(category.name),
            if (fillCategory == category) AmountFormulaPresentationModel2(amountFormula) else AmountFormulaPresentationModel1(amountFormula, onNewAmountFormula),
            CheckboxVMItem(initialValue = fillCategory == category, onCheckChanged = { if (it) userSetFillCategory(category) }),
        )
    }
}

