package com.tminus1010.budgetvalue.transactions.domain.models

import com.tminus1010.budgetvalue.transactions.models.Transaction

class TransactionsDomainModel(private val transactions: List<Transaction>) {
    val transactionsSortedByDate by lazy {
        transactions
            .sortedBy { it.date }
            .reversed()
    }
    val mostRecentSpend
        get() = transactionsSortedByDate
            .firstOrNull { it.isSpend }

    val mostRecentUncategorizedSpend
        get() = transactionsSortedByDate
            .firstOrNull { it.isSpend && it.isUncategorized }
}