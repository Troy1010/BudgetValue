package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue.data.CategoriesRepo
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue.domain.withSearchText
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriesInteractor @Inject constructor(
    private val categoriesRepo: CategoriesRepo,
    private val categorizeMatchingTransactions: CategorizeMatchingTransactions,
) {
    suspend fun addDescriptionAndCategorize(category: Category, description: String): Int {
        val newTransactionMatcher = category.onImportTransactionMatcher.withSearchText(description)
        categoriesRepo.push(category.copy(onImportTransactionMatcher = newTransactionMatcher))
        return categorizeMatchingTransactions(newTransactionMatcher::isMatch, category::categorize)
    }
}