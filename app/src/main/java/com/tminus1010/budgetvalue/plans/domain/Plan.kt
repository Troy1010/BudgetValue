package com.tminus1010.budgetvalue.plans.domain

import com.tminus1010.budgetvalue._core.app.LocalDatePeriod
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.data.model.PlanDTO
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal

data class Plan(
    val localDatePeriod: LocalDatePeriod,
    val amount: BigDecimal,
    val categoryAmounts: Map<Category, BigDecimal>,
) {
    val defaultAmount = amount - categoryAmounts.values.sum()

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