package com.tminus1010.budgetvalue.model_domain

import com.tminus1010.budgetvalue.layer_domain.IDatePeriodGetter
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