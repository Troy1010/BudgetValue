package com.tminus1010.budgetvalue.model_data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tminus1010.budgetvalue.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.layer_data.TypeConverter
import com.tminus1010.budgetvalue.model_app.Reconciliation
import java.math.BigDecimal
import java.time.LocalDate

@Entity
data class ReconciliationReceived(
    val localDate: LocalDate,
    val amount: BigDecimal,
    val categoryAmounts: String?,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
) {
    fun toReconciliation(typeConverter: TypeConverter): Reconciliation {
        return Reconciliation(localDate, SourceHashMap(typeConverter.categoryAmounts(categoryAmounts), BigDecimal(0)), amount)
    }
}