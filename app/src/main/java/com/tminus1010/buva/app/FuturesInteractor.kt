package com.tminus1010.buva.app

import com.tminus1010.buva.data.FuturesRepo
import com.tminus1010.buva.domain.Future
import com.tminus1010.buva.domain.withSearchText
import javax.inject.Inject

class FuturesInteractor @Inject constructor(
    private val futuresRepo: FuturesRepo,
    private val categorizeTransactions: CategorizeTransactions,
) {
    /**
     * returns how many transactions were categorized
     */
    suspend fun addDescriptionToFutureAndCategorize(description: String, future: Future): Int {
        val newTransactionMatcher = future.onImportTransactionMatcher.withSearchText(description)
        futuresRepo.push(future.copy(onImportTransactionMatcher = newTransactionMatcher))
        return categorizeTransactions(newTransactionMatcher::isMatch, future::categorize)
    }
}