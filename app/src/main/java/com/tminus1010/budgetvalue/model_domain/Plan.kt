package com.tminus1010.budgetvalue.model_domain

import com.tminus1010.budgetvalue.layer_domain.TypeConverter
import com.tminus1010.budgetvalue.model_data.Category
import com.tminus1010.budgetvalue.model_data.PlanReceived
import com.tminus1010.tmcommonkotlin.rx.extensions.sum
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

data class Plan(
    val localDatePeriod: Observable<LocalDatePeriod>,
    override val defaultAmount: BigDecimal,
    override val categoryAmounts: Map<Category, BigDecimal>,
) : IAmountAndCA {
    fun toPlanReceived(typeConverter: TypeConverter): PlanReceived {
        return PlanReceived(
            localDatePeriod.blockingFirst().startDate,
            localDatePeriod.blockingFirst().endDate,
            defaultAmount,
            typeConverter.toString(categoryAmounts)
        )
    }

    override val amount get() = categoryAmounts.values.sum() + defaultAmount
}