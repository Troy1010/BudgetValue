package com.tminus1010.budgetvalue.features.transactions

import com.tminus1010.budgetvalue.features_shared.IDatePeriodGetter
import com.tminus1010.budgetvalue.features_shared.history.IHistoryColumn
import com.tminus1010.budgetvalue.middleware.LocalDatePeriod
import com.tminus1010.budgetvalue.features.categories.Category
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import com.tminus1010.tmcommonkotlin.misc.extensions.toDisplayStr
import java.math.BigDecimal

data class Block(
    val datePeriod: LocalDatePeriod,
    val amount: BigDecimal,
    override val categoryAmounts: Map<Category, BigDecimal>
) : IHistoryColumn {
    override val title = "Actual"

    override fun subTitle(datePeriodGetter: IDatePeriodGetter): String? =
        if (datePeriod == datePeriodGetter.currentDatePeriod())
            "Current"
        else
            datePeriod.startDate.toDisplayStr()

    override val defaultAmount get() = amount - categoryAmounts.values.sum()
}