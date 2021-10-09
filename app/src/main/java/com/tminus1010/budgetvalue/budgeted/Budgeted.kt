package com.tminus1010.budgetvalue.budgeted

import com.tminus1010.budgetvalue._core.app.CategoryAmounts
import com.tminus1010.budgetvalue.categories.models.Category
import java.math.BigDecimal

class Budgeted(
    val categoryAmounts: Map<Category, BigDecimal>,
    val totalAmount: BigDecimal,
) {
    val defaultAmount: BigDecimal = CategoryAmounts(categoryAmounts).defaultAmount(totalAmount)
}