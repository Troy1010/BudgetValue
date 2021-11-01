package com.tminus1010.budgetvalue.plans.domain

import com.tminus1010.budgetvalue._core.app.CategoryAmounts
import com.tminus1010.budgetvalue._core.app.LocalDatePeriod
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal

data class Plan2(
    val localDatePeriod: LocalDatePeriod,
    val amount: BigDecimal,
    val categoryAmounts: CategoryAmounts,
) {
    val defaultAmount = amount - categoryAmounts.values.sum()
}