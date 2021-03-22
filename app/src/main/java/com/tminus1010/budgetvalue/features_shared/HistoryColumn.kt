package com.tminus1010.budgetvalue.features_shared

import com.tminus1010.budgetvalue.features.categories.Category
import java.math.BigDecimal

data class HistoryColumn(
    override val title: String,
    val subTitle: String? = null,
    override val defaultAmount: BigDecimal,
    override val categoryAmounts: Map<Category, BigDecimal>,
) : IHistoryColumn {
    override fun subTitle(datePeriodGetter: IDatePeriodGetter): String? = subTitle
}