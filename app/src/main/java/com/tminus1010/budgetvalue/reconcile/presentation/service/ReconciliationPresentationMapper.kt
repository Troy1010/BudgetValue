package com.tminus1010.budgetvalue.reconcile.presentation.service

import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.reconcile.presentation.model.CategoryAmountVMItem
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import javax.inject.Inject

class ReconciliationPresentationMapper @Inject constructor() {
    fun getCategoryAmountVMItems(
        categoryAmounts: Map<Category, BigDecimal>,
        onDone: (Category, String) -> Unit,
    ): Map<Category, CategoryAmountVMItem> {
        return categoryAmounts.mapValues {
            CategoryAmountVMItem(it.key, it.value, onDone)
        }
    }
}