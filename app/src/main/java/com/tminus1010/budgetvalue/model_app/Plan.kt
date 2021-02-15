package com.tminus1010.budgetvalue.model_app

import com.tminus1010.budgetvalue.extensions.sum
import com.tminus1010.budgetvalue.layer_data.TypeConverter
import com.tminus1010.budgetvalue.model_data.PlanReceived
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
            typeConverter.string(categoryAmounts)
        )
    }

    override val amount get() = categoryAmounts.values.sum() + defaultAmount
}