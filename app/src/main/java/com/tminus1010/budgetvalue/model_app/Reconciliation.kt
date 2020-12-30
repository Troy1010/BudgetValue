package com.tminus1010.budgetvalue.model_app

import com.tminus1010.budgetvalue.SourceHashMap
import com.tminus1010.budgetvalue.model_data.ReconciliationReceived
import java.math.BigDecimal
import java.time.LocalDate

data class Reconciliation(
    val localDate: LocalDate,
    val categoryAmounts: SourceHashMap<Category, BigDecimal>
) {
    fun toReconciliationReceived(amount: BigDecimal): ReconciliationReceived {
        return ReconciliationReceived(
            localDate = localDate,
            categoryAmounts = categoryAmounts.mapKeys { it.key.name }.toMap(),
            amount = amount)
    }
}