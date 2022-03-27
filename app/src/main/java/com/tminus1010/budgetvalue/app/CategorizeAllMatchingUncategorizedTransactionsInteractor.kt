package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue._unrestructured.transactions.app.Transaction
import com.tminus1010.budgetvalue._unrestructured.transactions.data.repo.TransactionsRepo
import javax.inject.Inject

class CategorizeAllMatchingUncategorizedTransactionsInteractor @Inject constructor(
    private val transactionsInteractor: TransactionsInteractor,
    private val transactionsRepo: TransactionsRepo,
) {
    suspend operator fun invoke(isMatch: (Transaction) -> Boolean, categorize: (Transaction) -> Transaction): Int {
        return transactionsInteractor.uncategorizedSpends.value
            .filter(isMatch)
            .onEach { transactionsRepo.update2(categorize(it)) }
            .fold(0) { acc, v -> acc + 1 }
    }
}