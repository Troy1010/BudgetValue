package com.tminus1010.budgetvalue.plans

import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue._shared.domain.IDatePeriodGetter
import com.tminus1010.budgetvalue.history.IHistoryColumnData
import com.tminus1010.budgetvalue._core.middleware.LocalDatePeriod
import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import com.tminus1010.tmcommonkotlin.misc.extensions.toDisplayStr
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

data class Plan(
    val localDatePeriod: Observable<LocalDatePeriod>,
    val amount: BigDecimal,
    override val categoryAmounts: Map<Category, BigDecimal>,
) : IHistoryColumnData {
    override val defaultAmount = amount - categoryAmounts.values.sum()
    override val title get() = "Plan"
    fun isCurrent(datePeriodGetter: IDatePeriodGetter) = localDatePeriod.blockingFirst() == datePeriodGetter.currentDatePeriod()
    override fun subTitle(datePeriodGetter: IDatePeriodGetter): String? =
        if (isCurrent(datePeriodGetter)) "Current" else
            localDatePeriod.blockingFirst().startDate.toDisplayStr()

    fun toDTO(categoryAmountsConverter: CategoryAmountsConverter): PlanDTO =
        PlanDTO(
            localDatePeriod.blockingFirst().startDate,
            localDatePeriod.blockingFirst().endDate,
            amount,
            categoryAmountsConverter.toString(categoryAmounts)
        )

    companion object {
        fun fromDTO(planDTO: PlanDTO, categoryAmountsConverter: CategoryAmountsConverter) =
            planDTO.run {
                Plan(
                    Observable.just(LocalDatePeriod(startDate, endDate)),
                    amount,
                    categoryAmountsConverter.toCategoryAmount(categoryAmounts)
                )
            }
    }
}