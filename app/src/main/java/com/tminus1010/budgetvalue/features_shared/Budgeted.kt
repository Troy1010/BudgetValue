package com.tminus1010.budgetvalue.features_shared

import com.tminus1010.budgetvalue.features.categories.Category
import java.math.BigDecimal

class Budgeted(
    override val categoryAmounts: Map<Category, BigDecimal>,
    override val defaultAmount: BigDecimal,
) : IHistoryColumn {
    override val title: String = "Budgeted"
    override fun subTitle(datePeriodGetter: IDatePeriodGetter): String? = null
}