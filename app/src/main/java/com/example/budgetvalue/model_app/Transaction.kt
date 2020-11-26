package com.example.budgetvalue.model_app

import com.example.budgetvalue.model_data.TransactionReceived
import com.example.budgetvalue.sum
import java.math.BigDecimal
import java.time.LocalDate

data class Transaction(
    var date: LocalDate,
    var description: String,
    var amount: BigDecimal,
    val categoryAmounts: Map<Category, BigDecimal> = hashMapOf(),
    val id: Int = 0
) {
    constructor(transactionReceived: TransactionReceived, categoryParser: ICategoryParser): this(
        transactionReceived.date,
        transactionReceived.description,
        transactionReceived.amount,
        transactionReceived.categoryAmounts.mapKeys { categoryParser.parseCategory(it.key) },
        transactionReceived.id
    )

    val isUncategorized: Boolean
        get() = categoryAmounts.isNullOrEmpty()
    val isSpend:Boolean
        get() = amount < BigDecimal.ZERO
    val uncategorizedAmount: BigDecimal
        get() {
            return amount - categoryAmounts.values.sum()
        }
    fun toTransactionReceived(): TransactionReceived {
        return TransactionReceived(this)
    }
}