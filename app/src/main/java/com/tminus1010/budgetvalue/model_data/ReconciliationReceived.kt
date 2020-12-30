package com.tminus1010.budgetvalue.model_data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tminus1010.budgetvalue.SourceHashMap
import com.tminus1010.budgetvalue.extensions.toHashMap
import com.tminus1010.budgetvalue.model_app.ICategoryParser
import com.tminus1010.budgetvalue.model_app.Transaction
import com.tminus1010.budgetvalue.extensions.sum
import com.tminus1010.budgetvalue.model_app.Reconciliation
import java.math.BigDecimal
import java.time.LocalDate

@Entity
data class ReconciliationReceived(
    val localDate: LocalDate,
    val amount: BigDecimal,
    val categoryAmounts: Map<String, BigDecimal> = emptyMap(),
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
) {
    fun toReconciliation(categoryParser: ICategoryParser): Reconciliation {
        return Reconciliation(localDate, SourceHashMap(categoryAmounts.mapKeys { categoryParser.parseCategory(it.key) }))
    }
}