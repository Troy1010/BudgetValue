package com.tminus1010.buva.app

import com.tminus1010.buva.domain.Transaction
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CategorizeTransactions @Inject constructor(
    private val transactionsInteractor: TransactionsInteractor,
) {
    suspend operator fun invoke(isMatch: (Transaction) -> Boolean, categorize: (Transaction) -> Transaction): Int {
        return transactionsInteractor.uncategorizedSpends.first()
            .filter(isMatch)
            .map { categorize(it) }
            .also { transactionsInteractor.push(it) }
            .size
    }
}