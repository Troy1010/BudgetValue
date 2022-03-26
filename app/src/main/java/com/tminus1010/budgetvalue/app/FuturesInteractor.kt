package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue.data.FuturesRepo
import com.tminus1010.budgetvalue.domain.Future
import com.tminus1010.budgetvalue.domain.TransactionMatcher
import javax.inject.Inject

class FuturesInteractor @Inject constructor(
    private val futuresRepo: FuturesRepo,
    private val categorizeAllMatchingUncategorizedTransactionsInteractor: CategorizeAllMatchingUncategorizedTransactionsInteractor,
) {
    /**
     * returns how many transactions were categorized
     */
    suspend fun addTransactionDescriptionToFuture(description: String, future: Future): Int {
        val newTransactionMatcher =
            when (future.onImportMatcher) {
                is TransactionMatcher.Multiple -> TransactionMatcher.Multiple(future.onImportMatcher.transactionMatchers.plus(TransactionMatcher.SearchText(description)))
                else -> TransactionMatcher.Multiple(future.onImportMatcher, TransactionMatcher.SearchText(description))
            }
        futuresRepo.push(future.copy(onImportMatcher = newTransactionMatcher))
        return categorizeAllMatchingUncategorizedTransactionsInteractor.categorizeAllMatchingUncategorizedTransactions(newTransactionMatcher::isMatch, future::categorize)
    }
}