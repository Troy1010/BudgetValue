package com.tminus1010.budgetvalue.model_domain

import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal

data class Block(
    val datePeriod: LocalDatePeriod,
    override val amount: BigDecimal,
    override val categoryAmounts: Map<Category, BigDecimal>
) : IAmountAndCA {
    override val defaultAmount get() = amount - categoryAmounts.values.sum()
}