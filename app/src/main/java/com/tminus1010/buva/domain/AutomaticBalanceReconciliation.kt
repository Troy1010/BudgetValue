package com.tminus1010.buva.domain

import java.math.BigDecimal

class AutomaticBalanceReconciliation(
    override val categoryAmounts: CategoryAmounts,
    override val total: BigDecimal,
) : CategoryAmountsAndTotal.FromTotal(categoryAmounts, total)