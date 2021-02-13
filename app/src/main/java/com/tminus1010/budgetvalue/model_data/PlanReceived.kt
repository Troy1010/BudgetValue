package com.tminus1010.budgetvalue.model_data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tminus1010.budgetvalue.layer_data.TypeConverter
import com.tminus1010.budgetvalue.model_app.LocalDatePeriod
import com.tminus1010.budgetvalue.model_app.Plan
import com.tminus1010.budgetvalue.model_app.Reconciliation
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import java.time.LocalDate

@Entity
data class PlanReceived(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val amount: BigDecimal,
    val categoryAmounts: String?,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
) {
    fun toPlan(typeConverter: TypeConverter) =
        Plan(Observable.just(LocalDatePeriod(startDate, endDate)),
            amount,
            typeConverter.categoryAmounts(categoryAmounts))
}
