package com.tminus1010.budgetvalue.features.plans

import com.tminus1010.budgetvalue.features.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.features_shared.IDatePeriodGetter
import com.tminus1010.budgetvalue.features_shared.history.IHistoryColumn
import com.tminus1010.budgetvalue.middleware.LocalDatePeriod
import com.tminus1010.budgetvalue.features.categories.Category
import com.tminus1010.tmcommonkotlin.misc.extensions.toDisplayStr
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

data class Plan(
    val localDatePeriod: Observable<LocalDatePeriod>,
    override val defaultAmount: BigDecimal,
    override val categoryAmounts: Map<Category, BigDecimal>,
) : IHistoryColumn {
    override val title get() = "Plan"
    override fun subTitle(datePeriodGetter: IDatePeriodGetter): String? =
        if (localDatePeriod.blockingFirst() == datePeriodGetter.currentDatePeriod())
            "Current"
        else
            localDatePeriod.blockingFirst().startDate.toDisplayStr()

    fun toDTO(categoryAmountsConverter: CategoryAmountsConverter): PlanDTO =
        PlanDTO(
            localDatePeriod.blockingFirst().startDate,
            localDatePeriod.blockingFirst().endDate,
            defaultAmount,
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