package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue.domain.Transaction
import javax.inject.Inject

class CategorizeMatchingUncategorizedTransactions @Inject constructor(
    private val transactionsInteractor: TransactionsInteractor,
) {
    suspend operator fun invoke(isMatch: (Transaction) -> Boolean, categorize: (Transaction) -> Transaction): Int {
        return transactionsInteractor.uncategorizedSpends.value
            .filter(isMatch)
            .onEach { transactionsInteractor.push(categorize(it)) }
            .fold(0) { acc, v -> acc + 1 }
    }
}