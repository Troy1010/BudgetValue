package com.tminus1010.budgetvalue.model_app

import com.tminus1010.budgetvalue.model_data.TransactionReceived
import com.tminus1010.budgetvalue.extensions.sum
import java.math.BigDecimal
import java.time.LocalDate

data class Transaction(
    var date: LocalDate,
    var description: String,
    val id: Int = 0,
    val amount: BigDecimal,
    val categoryAmounts: Map<Category, BigDecimal>,
) {
    constructor(transactionReceived: TransactionReceived, categoryParser: ICategoryParser) : this(
        transactionReceived.date,
        transactionReceived.description,
        transactionReceived.id,
        transactionReceived.amount,
        transactionReceived.categoryAmounts.mapKeys { categoryParser.parseCategory(it.key) },
    )

    val isUncategorized get() = categoryAmounts.isNullOrEmpty()
    val isSpend get() = amount < BigDecimal.ZERO
    val defaultAmount get() = amount - categoryAmounts.values.sum()
    fun toTransactionReceived(): TransactionReceived {
        return TransactionReceived(this)
    }
}