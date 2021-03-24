package com.tminus1010.budgetvalue.features_shared.history

import com.tminus1010.budgetvalue.features.categories.Category
import com.tminus1010.budgetvalue.features_shared.IDatePeriodGetter
import java.math.BigDecimal

interface IHistoryColumnData {
    val title: String
    fun subTitle(datePeriodGetter: IDatePeriodGetter): String?
    val defaultAmount: BigDecimal
    val categoryAmounts: Map<Category, BigDecimal>
}