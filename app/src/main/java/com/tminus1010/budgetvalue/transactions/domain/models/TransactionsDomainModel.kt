package com.tminus1010.budgetvalue.transactions.domain.models

import com.tminus1010.budgetvalue.transactions.models.Transaction

class TransactionsDomainModel(private val transactions: List<Transaction>) {
    private val sortedByDateTransactions by lazy {
        transactions
            .sortedBy { it.date }
            .reversed()
    }
    val mostRecentSpend
        get() = sortedByDateTransactions
            .firstOrNull { it.isSpend }

    val mostRecentUncategorizedSpend
        get() = sortedByDateTransactions
            .firstOrNull { it.isSpend && it.isUncategorized }
}