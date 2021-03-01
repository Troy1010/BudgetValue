package com.tminus1010.budgetvalue.model_data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tminus1010.budgetvalue.layer_domain.TypeConverter
import com.tminus1010.budgetvalue.model_domain.LocalDatePeriod
import com.tminus1010.budgetvalue.model_domain.Plan
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import java.time.LocalDate

@Entity
data class PlanDTO(
    @PrimaryKey
    val startDate: LocalDate,
    val endDate: LocalDate,
    val amount: BigDecimal,
    val categoryAmounts: String?,
) {
    fun toPlan(typeConverter: TypeConverter) =
        Plan(Observable.just(LocalDatePeriod(startDate, endDate)),
            amount,
            typeConverter.toCategoryAmount(categoryAmounts))
}
