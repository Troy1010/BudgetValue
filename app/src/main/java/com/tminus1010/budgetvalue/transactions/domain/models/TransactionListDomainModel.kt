package com.tminus1010.budgetvalue.transactions.domain.models

import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.budgetvalue.transactions.models.TransactionDTO

class TransactionListDomainModel(transactionListDTO: List<TransactionDTO>, categoryAmountsConverter: CategoryAmountsConverter) {
    val transactions: List<Transaction> =
        transactionListDTO.map { Transaction.fromDTO(it, categoryAmountsConverter) }
            .sortedBy { it.date }
            .reversed()

    val mostRecentSpend
        get() = transactions
            .firstOrNull { it.isSpend }

    val mostRecentUncategorizedSpend
        get() = transactions
            .firstOrNull { it.isSpend && it.isUncategorized }
}