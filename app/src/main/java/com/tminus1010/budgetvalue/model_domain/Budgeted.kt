package com.tminus1010.budgetvalue.model_domain

import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal

class Budgeted(
    override val categoryAmounts: Map<Category, BigDecimal>,
    override val defaultAmount: BigDecimal,
) : IAmountAndCA {
    override val amount get() = defaultAmount + categoryAmounts.values.sum()
}