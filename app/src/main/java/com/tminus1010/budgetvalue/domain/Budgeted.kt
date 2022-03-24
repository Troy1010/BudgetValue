package com.tminus1010.budgetvalue.domain

import java.math.BigDecimal

class Budgeted(
    val categoryAmounts: Map<Category, BigDecimal>,
    val totalAmount: BigDecimal,
) {
    val defaultAmount: BigDecimal = CategoryAmounts(categoryAmounts).defaultAmount(totalAmount)
}