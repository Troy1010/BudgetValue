package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue.data.FuturesRepo
import com.tminus1010.budgetvalue.domain.Future
import com.tminus1010.budgetvalue.domain.TransactionMatcher
import com.tminus1010.budgetvalue.domain.withSearchText
import javax.inject.Inject

class FuturesInteractor @Inject constructor(
    private val futuresRepo: FuturesRepo,
    private val categorizeMatchingTransactions: CategorizeMatchingTransactions,
) {
    /**
     * returns how many transactions were categorized
     */
    suspend fun addDescriptionToFutureAndCategorize(description: String, future: Future): Int {
        val newTransactionMatcher = future.onImportTransactionMatcher.withSearchText(description)
        futuresRepo.push(future.copy(onImportTransactionMatcher = newTransactionMatcher))
        return categorizeMatchingTransactions(newTransactionMatcher::isMatch, future::categorize)
    }
}