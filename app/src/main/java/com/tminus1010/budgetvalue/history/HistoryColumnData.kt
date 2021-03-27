package com.tminus1010.budgetvalue.history

import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.aa_shared.domain.IDatePeriodGetter
import java.math.BigDecimal

data class HistoryColumnData(
    override val title: String,
    val subTitle: String? = null,
    override val defaultAmount: BigDecimal,
    override val categoryAmounts: Map<Category, BigDecimal>,
) : IHistoryColumnData {
    override fun subTitle(datePeriodGetter: IDatePeriodGetter): String? = subTitle
}