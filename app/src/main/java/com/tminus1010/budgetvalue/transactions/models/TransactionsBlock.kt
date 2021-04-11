package com.tminus1010.budgetvalue.transactions.models

import com.tminus1010.budgetvalue._shared.date_period_getter.IDatePeriodGetter
import com.tminus1010.budgetvalue.history.models.IHistoryColumnData
import com.tminus1010.budgetvalue._core.middleware.LocalDatePeriod
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import com.tminus1010.tmcommonkotlin.misc.extensions.toDisplayStr
import java.math.BigDecimal

data class TransactionsBlock(
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