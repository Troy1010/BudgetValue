package com.tminus1010.budgetvalue.model_app

import com.tminus1010.budgetvalue.extensions.sum
import java.math.BigDecimal

data class Block(
    val datePeriod: LocalDatePeriod,
    val amount: BigDecimal,
    val categoryAmounts: Map<Category, BigDecimal>
) {
    val defaultAmount get() = amount - categoryAmounts.values.sum()
}