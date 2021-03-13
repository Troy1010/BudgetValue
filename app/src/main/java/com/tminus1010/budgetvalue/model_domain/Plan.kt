package com.tminus1010.budgetvalue.model_domain

import com.tminus1010.budgetvalue.layer_domain.CategoryAmountsConverter
import com.tminus1010.budgetvalue.model_data.PlanDTO
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

data class Plan(
    val localDatePeriod: Observable<LocalDatePeriod>,
    override val defaultAmount: BigDecimal,
    override val categoryAmounts: Map<Category, BigDecimal>,
) : IAmountAndCA {
    fun toDTO(categoryAmountsConverter: CategoryAmountsConverter): PlanDTO =
        PlanDTO(
            localDatePeriod.blockingFirst().startDate,
            localDatePeriod.blockingFirst().endDate,
            defaultAmount,
            categoryAmountsConverter.toString(categoryAmounts)
        )

    override val amount get() = categoryAmounts.values.sum() + defaultAmount

    companion object {
        fun fromDTO(planDTO: PlanDTO, categoryAmountsConverter: CategoryAmountsConverter) =
            planDTO.run {
                Plan(Observable.just(LocalDatePeriod(startDate, endDate)),
                    amount,
                    categoryAmountsConverter.toCategoryAmount(categoryAmounts))
            }
    }
}