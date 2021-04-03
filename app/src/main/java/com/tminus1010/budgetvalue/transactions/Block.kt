package com.tminus1010.budgetvalue.transactions

import com.tminus1010.budgetvalue._core.shared_features.date_period_getter.IDatePeriodGetter
import com.tminus1010.budgetvalue.history.IHistoryColumnData
import com.tminus1010.budgetvalue._core.middleware.LocalDatePeriod
import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import com.tminus1010.tmcommonkotlin.misc.extensions.toDisplayStr
import java.math.BigDecimal

data class Block(
    val datePeriod: LocalDatePeriod,
    val amount: BigDecimal,
    override val categoryAmounts: Map<Category, BigDecimal>
) : IHistoryColumnData {
    override val title = "Actual"

    override fun subTitle(datePeriodGetter: IDatePeriodGetter): String? =
        if (datePeriod == datePeriodGetter.currentDatePeriod())
            "Current"
        else
            datePeriod.startDate.toDisplayStr()

    override val defaultAmount get() = amount - categoryAmounts.values.sum()
}