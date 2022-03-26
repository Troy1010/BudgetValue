package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue._unrestructured.transactions.app.Transaction
import com.tminus1010.budgetvalue._unrestructured.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.budgetvalue._unrestructured.transactions.data.repo.TransactionsRepo
import javax.inject.Inject

class CategorizeAllMatchingUncategorizedTransactionsInteractor @Inject constructor(
    private val transactionsInteractor: TransactionsInteractor,
    private val transactionsRepo: TransactionsRepo,
) {
    suspend fun categorizeAllMatchingUncategorizedTransactions(predicate: (Transaction) -> Boolean, categorization: (Transaction) -> Transaction): Int {
        var counter = 0
        transactionsInteractor.uncategorizedSpends2.value
            .filter { predicate(it) }
            .forEach { transactionsRepo.update2(categorization(it)); counter++ }
        return counter
    }
}