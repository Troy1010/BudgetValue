package com.tminus1010.budgetvalue.history

import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.aa_shared.IDatePeriodGetter
import java.math.BigDecimal

interface IHistoryColumnData {
    val title: String
    fun subTitle(datePeriodGetter: IDatePeriodGetter): String?
    val defaultAmount: BigDecimal
    val categoryAmounts: Map<Category, BigDecimal>
}