package com.tminus1010.budgetvalue.features.history

import com.tminus1010.budgetvalue.features.categories.Category
import com.tminus1010.budgetvalue.features_shared.IDatePeriodGetter
import java.math.BigDecimal

data class HistoryColumnData(
    override val title: String,
    val subTitle: String? = null,
    override val defaultAmount: BigDecimal,
    override val categoryAmounts: Map<Category, BigDecimal>,
) : IHistoryColumnData {
    override fun subTitle(datePeriodGetter: IDatePeriodGetter): String? = subTitle
}