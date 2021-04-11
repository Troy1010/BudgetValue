package com.tminus1010.budgetvalue.history.models

import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue._shared.date_period_getter.IDatePeriodGetter
import java.math.BigDecimal

data class HistoryColumnData(
    override val title: String,
    val subTitle: String? = null,
    override val defaultAmount: BigDecimal,
    override val categoryAmounts: Map<Category, BigDecimal>,
) : IHistoryColumnData {
    override fun subTitle(datePeriodGetter: IDatePeriodGetter): String? = subTitle
}