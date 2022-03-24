package com.tminus1010.budgetvalue.domain.plan

import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue._unrestructured.reconcile.domain.CategoryAmountsAndTotal
import java.math.BigDecimal

data class ActivePlan(
    override val total: BigDecimal,
    override val categoryAmounts: CategoryAmounts,
) : CategoryAmountsAndTotal.FromTotal(categoryAmounts, total)