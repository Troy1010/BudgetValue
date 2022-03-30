package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue.data.FuturesRepo
import com.tminus1010.budgetvalue.domain.Future
import com.tminus1010.budgetvalue.domain.TransactionMatcher
import javax.inject.Inject

class FuturesInteractor @Inject constructor(
    private val futuresRepo: FuturesRepo,
    private val categorizeMatchingTransactions: CategorizeMatchingTransactions,
) {
    /**
     * returns how many transactions were categorized
     */
    suspend fun addDescriptionToFutureAndCategorize(description: String, future: Future): Int {
        val newTransactionMatcher =
            when (future.onImportTransactionMatcher) {
                is TransactionMatcher.Multi -> TransactionMatcher.Multi(future.onImportTransactionMatcher.transactionMatchers.plus(TransactionMatcher.SearchText(description)))
                else -> TransactionMatcher.Multi(future.onImportTransactionMatcher, TransactionMatcher.SearchText(description))
            }
        futuresRepo.push(future.copy(onImportTransactionMatcher = newTransactionMatcher))
        return categorizeMatchingTransactions(newTransactionMatcher::isMatch, future::categorize)
    }
}