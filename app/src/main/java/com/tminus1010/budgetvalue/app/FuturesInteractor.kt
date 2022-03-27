package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue.data.FuturesRepo
import com.tminus1010.budgetvalue.domain.Future
import com.tminus1010.budgetvalue.domain.TransactionMatcher
import javax.inject.Inject

class FuturesInteractor @Inject constructor(
    private val futuresRepo: FuturesRepo,
    private val categorizeMatchingUncategorizedTransactions: CategorizeMatchingUncategorizedTransactions,
) {
    /**
     * returns how many transactions were categorized
     */
    suspend fun addDescriptionToFutureAndCategorize(description: String, future: Future): Int {
        val newTransactionMatcher =
            when (future.onImportMatcher) {
                is TransactionMatcher.Multi -> TransactionMatcher.Multi(future.onImportMatcher.transactionMatchers.plus(TransactionMatcher.SearchText(description)))
                else -> TransactionMatcher.Multi(future.onImportMatcher, TransactionMatcher.SearchText(description))
            }
        futuresRepo.push(future.copy(onImportMatcher = newTransactionMatcher))
        return categorizeMatchingUncategorizedTransactions(newTransactionMatcher::isMatch, future::categorize)
    }
}