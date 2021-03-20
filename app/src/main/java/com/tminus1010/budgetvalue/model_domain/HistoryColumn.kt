package com.tminus1010.budgetvalue.model_domain

import com.tminus1010.budgetvalue.layer_domain.IDatePeriodGetter
import java.math.BigDecimal

data class HistoryColumn(
    override val title: String,
    val subTitle: String? = null,
    override val defaultAmount: BigDecimal,
    override val categoryAmounts: Map<Category, BigDecimal>,
) : IHistoryColumn {
    override fun subTitle(datePeriodGetter: IDatePeriodGetter): String? = subTitle
}