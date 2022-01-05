package com.tminus1010.budgetvalue.plans.domain

import com.tminus1010.budgetvalue._core.domain.CategoryAmounts
import com.tminus1010.budgetvalue.reconcile.domain.CategoryAmountsAndTotal
import java.math.BigDecimal

data class ActivePlan(
    override val total: BigDecimal,
    override val categoryAmounts: CategoryAmounts,
) : CategoryAmountsAndTotal.FromTotal(categoryAmounts, total)