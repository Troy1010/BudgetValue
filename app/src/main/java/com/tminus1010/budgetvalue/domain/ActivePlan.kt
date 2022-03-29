package com.tminus1010.budgetvalue.domain

import java.math.BigDecimal

data class ActivePlan(
    override val total: BigDecimal,
    override val categoryAmounts: CategoryAmounts,
) : CategoryAmountsAndTotal.FromTotal(categoryAmounts, total)