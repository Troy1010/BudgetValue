package com.tminus1010.budgetvalue.model_domain

import com.tminus1010.budgetvalue.layer_domain.IDatePeriodGetter
import java.math.BigDecimal

interface IHistoryColumn {
    val title: String
    fun subTitle(datePeriodGetter: IDatePeriodGetter): String?
    val defaultAmount: BigDecimal
    val categoryAmounts: Map<Category, BigDecimal>
}