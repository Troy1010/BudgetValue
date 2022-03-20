package com.tminus1010.budgetvalue.all_features.presentation.model

import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.all_features.app.model.Category
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
            CheckboxVMItem2(initialValue = fillCategory == category, onChecked = { userSetFillCategory(category) }),
        )
    }
}

