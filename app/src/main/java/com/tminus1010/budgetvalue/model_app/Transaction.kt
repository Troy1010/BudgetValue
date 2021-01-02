package com.tminus1010.budgetvalue.model_app

import com.tminus1010.budgetvalue.extensions.sum
import com.tminus1010.budgetvalue.extensions.toHashMap
import com.tminus1010.budgetvalue.model_data.TransactionReceived
import java.math.BigDecimal
import java.time.LocalDate

data class Transaction(
    val date: LocalDate,
    val description: String,
    val amount: BigDecimal,
    val categoryAmounts: Map<Category, BigDecimal>,
    val id: Int = 0,
) {
    val isUncategorized get() = categoryAmounts.isNullOrEmpty()
    val isSpend get() = amount < BigDecimal.ZERO
    val defaultAmount get() = amount - categoryAmounts.values.sum()
    fun toTransactionReceived(): TransactionReceived {
        return TransactionReceived(
            date,
            description,
            amount,
            categoryAmounts.mapKeys { it.key.name }.toHashMap(),
            id,
        )
    }
}