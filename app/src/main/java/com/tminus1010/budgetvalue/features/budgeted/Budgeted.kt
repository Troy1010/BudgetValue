package com.tminus1010.budgetvalue.features.budgeted

import com.tminus1010.budgetvalue.features.categories.Category
import com.tminus1010.budgetvalue.features.history.IHistoryColumnData
import com.tminus1010.budgetvalue.features_shared.IDatePeriodGetter
import java.math.BigDecimal

class Budgeted(
    override val categoryAmounts: Map<Category, BigDecimal>,
    override val defaultAmount: BigDecimal,
) : IHistoryColumnData {
    override val title: String = "Budgeted"
    override fun subTitle(datePeriodGetter: IDatePeriodGetter): String? = null
}