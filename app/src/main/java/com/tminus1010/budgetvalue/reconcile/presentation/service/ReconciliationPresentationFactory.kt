package com.tminus1010.budgetvalue.reconcile.presentation.service

import com.tminus1010.budgetvalue._core.presentation.model.CategoryAmountPresentationModel
import com.tminus1010.budgetvalue.categories.models.Category
import java.math.BigDecimal
import javax.inject.Inject

class ReconciliationPresentationFactory @Inject constructor() {
    fun getCategoryAmountVMItems(
        categoryAmounts: Map<Category, BigDecimal>,
        onDone: (Category, String) -> Unit,
    ): Map<Category, CategoryAmountPresentationModel> {
        return categoryAmounts.mapValues {
            CategoryAmountPresentationModel(it.key, it.value, onDone)
        }
    }

    fun getCategoryAmountVMItem(
        category: Category,
        amount: BigDecimal,
        onDone: (Category, String) -> Unit,
    ): CategoryAmountPresentationModel {
        return CategoryAmountPresentationModel(category, amount, onDone)
    }
}