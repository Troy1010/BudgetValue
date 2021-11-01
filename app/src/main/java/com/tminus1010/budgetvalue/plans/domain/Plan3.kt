package com.tminus1010.budgetvalue.plans.domain

import com.tminus1010.budgetvalue._core.app.LocalDatePeriod
import com.tminus1010.budgetvalue.categories.models.Category
import java.math.BigDecimal

data class Plan3(
    val localDatePeriod: LocalDatePeriod,
    val amount: BigDecimal,
    val categoryAmounts: Map<Category, BigDecimal>,
)