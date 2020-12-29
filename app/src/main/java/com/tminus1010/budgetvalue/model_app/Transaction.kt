package com.tminus1010.budgetvalue.model_app

import com.tminus1010.budgetvalue.model_data.TransactionReceived
import com.tminus1010.budgetvalue.extensions.sum
import java.math.BigDecimal
import java.time.LocalDate

data class Transaction(
    var date: LocalDate,
    var description: String,
    var amount: BigDecimal,
    val categoryAmounts: Map<Category, BigDecimal> = hashMapOf(),
    val id: Int = 0,
) {
    constructor(transactionReceived: TransactionReceived, categoryParser: ICategoryParser) : this(
        transactionReceived.date,
        transactionReceived.description,
        transactionReceived.amount,
        transactionReceived.categoryAmounts.mapKeys { categoryParser.parseCategory(it.key) },
        transactionReceived.id
    )

    val isUncategorized get() = categoryAmounts.isNullOrEmpty()
    val isSpend get() = amount < BigDecimal.ZERO
    val uncategorizedAmount get() = amount - categoryAmounts.values.sum()
    fun toTransactionReceived(): TransactionReceived {
        return TransactionReceived(this)
    }
}