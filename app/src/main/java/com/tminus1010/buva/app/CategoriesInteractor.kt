package com.tminus1010.buva.app

import com.tminus1010.buva.data.CategoriesRepo
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.withSearchText
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriesInteractor @Inject constructor(
    private val categoriesRepo: CategoriesRepo,
    private val categorizeTransactions: CategorizeTransactions,
) {
    suspend fun addDescriptionAndCategorize(category: Category, description: String): Int {
        val newTransactionMatcher = category.onImportTransactionMatcher.withSearchText(description)
        categoriesRepo.push(category.copy(onImportTransactionMatcher = newTransactionMatcher))
        return categorizeTransactions(newTransactionMatcher::isMatch, category::categorize)
    }
}