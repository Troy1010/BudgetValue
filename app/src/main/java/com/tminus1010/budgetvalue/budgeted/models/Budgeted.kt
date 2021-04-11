package com.tminus1010.budgetvalue.budgeted.models

import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.history.models.IHistoryColumnData
import com.tminus1010.budgetvalue._shared.date_period_getter.IDatePeriodGetter
import java.math.BigDecimal

class Budgeted(
    override val categoryAmounts: Map<Category, BigDecimal>,
    override val defaultAmount: BigDecimal,
) : IHistoryColumnData {
    override val title: String = "Budgeted"
    override fun subTitle(datePeriodGetter: IDatePeriodGetter): String? = null
}