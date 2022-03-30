package com.tminus1010.budgetvalue.ui.all_features

import com.tminus1010.budgetvalue.domain.TransactionMatcher
import com.tminus1010.budgetvalue.ui.all_features.model.SearchType
import javax.inject.Inject

class TransactionMatcherPresentationFactory @Inject constructor() {
    fun searchType(transactionMatcher: TransactionMatcher?): SearchType {
        return when (transactionMatcher) {
            is TransactionMatcher.SearchText,
            -> SearchType.DESCRIPTION
            is TransactionMatcher.ByValue,
            -> SearchType.TOTAL
            is TransactionMatcher.Multi,
            -> if (transactionMatcher.transactionMatchers.all { it is TransactionMatcher.SearchText })
                SearchType.DESCRIPTION
            else
                SearchType.DESCRIPTION_AND_TOTAL
            null,
            -> SearchType.NONE
        }
    }

    fun totalTitle(transactionMatcher: TransactionMatcher?): String {
        return when (searchType(transactionMatcher)) {
            SearchType.NONE,
            SearchType.DESCRIPTION,
            -> "Total Guess"
            SearchType.TOTAL,
            SearchType.DESCRIPTION_AND_TOTAL,
            -> "Exact Total"
        }
    }

    fun hasSearchTexts(transactionMatcher: TransactionMatcher?): Boolean {
        return listOf(SearchType.DESCRIPTION_AND_TOTAL, SearchType.DESCRIPTION).any { it == searchType(transactionMatcher) }
    }
}