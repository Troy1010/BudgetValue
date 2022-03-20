package com.tminus1010.budgetvalue.all_features.app.model

import com.tminus1010.budgetvalue.all_features.domain.CategoryAmounts
import java.math.BigDecimal

class Budgeted(
    val categoryAmounts: Map<Category, BigDecimal>,
    val totalAmount: BigDecimal,
) {
    val defaultAmount: BigDecimal = CategoryAmounts(categoryAmounts).defaultAmount(totalAmount)
}