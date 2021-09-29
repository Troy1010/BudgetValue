package com.tminus1010.budgetvalue.transactions.domain.models

import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.budgetvalue.transactions.models.TransactionDTO

class TransactionsAggregate(
    transactionsDTO: List<TransactionDTO>,
    categoryAmountsConverter: CategoryAmountsConverter
) {
    val transactions =
        transactionsDTO
            .map { Transaction.fromDTO(it, categoryAmountsConverter) }
            .sortedByDescending(Transaction::date)

    val spends by lazy {
        transactions
            .filter(Transaction::isSpend)
    }

    val mostRecentSpend by lazy {
        spends
            .firstOrNull()
    }

    val mostRecentUncategorizedSpend by lazy {
        spends
            .firstOrNull(Transaction::isUncategorized)
    }
}