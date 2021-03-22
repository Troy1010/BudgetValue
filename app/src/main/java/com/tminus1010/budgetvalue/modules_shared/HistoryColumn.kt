package com.tminus1010.budgetvalue.modules_shared

import com.tminus1010.budgetvalue.modules.categories.Category
import java.math.BigDecimal

data class HistoryColumn(
    override val title: String,
    val subTitle: String? = null,
    override val defaultAmount: BigDecimal,
    override val categoryAmounts: Map<Category, BigDecimal>,
) : IHistoryColumn {
    override fun subTitle(datePeriodGetter: IDatePeriodGetter): String? = subTitle
}