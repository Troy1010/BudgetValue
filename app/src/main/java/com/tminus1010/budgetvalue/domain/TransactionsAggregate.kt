package com.tminus1010.budgetvalue.domain

class TransactionsAggregate(
    transactions: List<Transaction>,
) {
    val transactions = transactions.sortedByDescending(Transaction::date)
    val spends by lazy { transactions.filter(Transaction::isSpend) }
    val mostRecentSpend by lazy { spends.firstOrNull() }
    val oldestSpend by lazy { spends.lastOrNull() }
    val mostRecentUncategorizedSpend by lazy { spends.firstOrNull(Transaction::isUncategorized) }
}