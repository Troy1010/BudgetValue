package com.example.budgetvalue.model_app

import com.example.budgetvalue.layer_ui.misc.sum
import com.example.budgetvalue.model_data.TransactionReceived
import java.math.BigDecimal
import java.time.LocalDate

data class Transaction(
    var date: LocalDate,
    var description: String,
    var amount: BigDecimal,
    val categoryAmounts: Map<Category, BigDecimal> = hashMapOf(),
    val id: Int = 0
) {
    constructor(transactionReceived: TransactionReceived, transformCategoryAction: (String) -> Category): this(
        transactionReceived.date,
        transactionReceived.description,
        transactionReceived.amount,
        transactionReceived.categoryAmounts.mapKeys { transformCategoryAction(it.key) },
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
}