package com.tminus1010.budgetvalue.model_app

import com.tminus1010.budgetvalue.layer_data.TypeConverter
import com.tminus1010.budgetvalue.model_data.PlanReceived
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

data class Plan(
    val localDatePeriod: Observable<LocalDatePeriod>,
    val defaultAmount: BigDecimal,
    val categoryAmounts: Map<Category, BigDecimal>,
) {
    fun toPlanReceived(typeConverter: TypeConverter): PlanReceived {
        return PlanReceived(
            localDatePeriod.blockingFirst().startDate,
            localDatePeriod.blockingFirst().endDate,
            defaultAmount,
            typeConverter.string(categoryAmounts)
        )
    }
}