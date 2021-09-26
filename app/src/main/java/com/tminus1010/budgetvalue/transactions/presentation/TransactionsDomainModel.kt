package com.tminus1010.budgetvalue.transactions.presentation

import com.tminus1010.budgetvalue.transactions.models.Transaction

class TransactionsDomainModel(private val transactions: List<Transaction>) {
    val mostRecentUncategorizedSpend
        get() = transactions
            .sortedBy { it.date }
            .reversed()
            .firstOrNull { it.isSpend && it.isUncategorized }
}