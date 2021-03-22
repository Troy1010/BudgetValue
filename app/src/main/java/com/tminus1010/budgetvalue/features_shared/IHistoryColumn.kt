package com.tminus1010.budgetvalue.features_shared

import com.tminus1010.budgetvalue.features.categories.Category
import java.math.BigDecimal

interface IHistoryColumn {
    val title: String
    fun subTitle(datePeriodGetter: IDatePeriodGetter): String?
    val defaultAmount: BigDecimal
    val categoryAmounts: Map<Category, BigDecimal>
}