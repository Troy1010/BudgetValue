package com.tminus1010.budgetvalue.model_app

import com.tminus1010.budgetvalue.layer_data.TypeConverter
import com.tminus1010.budgetvalue.model_data.Category
import com.tminus1010.budgetvalue.model_data.ReconciliationReceived
import java.math.BigDecimal
import java.time.LocalDate

data class Reconciliation(
    val localDate: LocalDate,
    val defaultAmount: BigDecimal,
    val categoryAmounts: Map<Category, BigDecimal>,
    val id: Int
) {
    fun toReconciliationReceived(typeConverter: TypeConverter, amount: BigDecimal): ReconciliationReceived {
        return ReconciliationReceived(
            localDate = localDate,
            categoryAmounts = typeConverter.string(categoryAmounts),
            amount = amount,
            id = id,)
    }
}