package com.tminus1010.budgetvalue.plans.models

import com.tminus1010.budgetvalue._core.middleware.LocalDatePeriod
import com.tminus1010.budgetvalue._shared.date_period_getter.IDatePeriodGetter
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.history.models.IHistoryColumnData
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal

data class Plan(
    val localDatePeriod: LocalDatePeriod,
    val amount: BigDecimal,
    override val categoryAmounts: Map<Category, BigDecimal>,
) : IHistoryColumnData {
    override val defaultAmount = amount - categoryAmounts.values.sum()
    override val title = "Plan"
    fun isCurrent(datePeriodGetter: IDatePeriodGetter) = localDatePeriod == datePeriodGetter.currentDatePeriod()
    override fun subTitle(datePeriodGetter: IDatePeriodGetter): String? =
        if (isCurrent(datePeriodGetter)) "Current" else
            localDatePeriod.startDate.toDisplayStr()

    fun toDTO(categoryAmountsConverter: CategoryAmountsConverter): PlanDTO =
        PlanDTO(
            localDatePeriod.startDate,
            localDatePeriod.endDate,
            amount,
            categoryAmountsConverter.toJson(categoryAmounts)
        )

    companion object {
        fun fromDTO(planDTO: PlanDTO, categoryAmountsConverter: CategoryAmountsConverter) = planDTO.run {
            Plan(
                LocalDatePeriod(startDate, endDate),
                amount,
                categoryAmountsConverter.toCategoryAmounts(categoryAmounts)
            )
        }
    }
}