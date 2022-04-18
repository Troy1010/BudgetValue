package com.tminus1010.buva.domain

class TransactionsAggregate(
    transactions: List<Transaction>,
) {
    val transactions = transactions.sortedBy { it.description }.sortedByDescending(Transaction::date)
    val spends by lazy { this.transactions.filter(Transaction::isSpend) }
    val mostRecentSpend by lazy { spends.firstOrNull() }
    val oldestSpend by lazy { spends.lastOrNull() }
    val mostRecentUncategorizedSpend by lazy { spends.firstOrNull(Transaction::isUncategorized) }
}