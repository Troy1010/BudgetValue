package com.tminus1010.budgetvalue.model_domain

import com.tminus1010.budgetvalue.layer_domain.TypeConverter
import com.tminus1010.budgetvalue.model_data.ReconciliationReceived
import java.math.BigDecimal
import java.time.LocalDate

data class Reconciliation(
    val localDate: LocalDate,
    val defaultAmount: BigDecimal,
    val categoryAmounts: Map<Category, BigDecimal>,
    val id: Int = 0
) {
    fun toReconciliationReceived(typeConverter: TypeConverter, amount: BigDecimal): ReconciliationReceived {
        return ReconciliationReceived(
            localDate = localDate,
            categoryAmounts = typeConverter.toString(categoryAmounts),
            amount = amount,
            id = id,)
    }
}