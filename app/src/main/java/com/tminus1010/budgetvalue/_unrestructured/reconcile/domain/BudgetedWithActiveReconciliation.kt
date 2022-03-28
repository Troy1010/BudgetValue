package com.tminus1010.budgetvalue._unrestructured.reconcile.domain

import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue.domain.CategoryAmountsAndTotal
import java.math.BigDecimal

class BudgetedWithActiveReconciliation(
    categoryAmounts: CategoryAmounts,
    total: BigDecimal,
    private val caValidation: (BigDecimal?) -> Boolean,
    private val defaultAmountValidation: (BigDecimal?) -> Boolean,
) : CategoryAmountsAndTotal.FromTotal(categoryAmounts, total) {
    val isDefaultAmountValid by lazy { defaultAmountValidation(defaultAmount) }
    val isAllValid by lazy { categoryAmounts.keys.map { isValid(it) }.all { it } && isDefaultAmountValid }

    fun isValid(category: Category): Boolean {
        return caValidation(categoryAmounts[category])
    }
}