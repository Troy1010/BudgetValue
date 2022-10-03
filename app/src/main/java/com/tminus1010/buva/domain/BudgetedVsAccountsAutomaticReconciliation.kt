package com.tminus1010.buva.domain

import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.buva.domain.CategoryAmountsAndTotal
import java.math.BigDecimal

class BudgetedVsAccountsAutomaticReconciliation(
    override val categoryAmounts: CategoryAmounts,
    override val total: BigDecimal,
) : CategoryAmountsAndTotal.FromTotal(categoryAmounts, total)