package com.tminus1010.buva.app

import com.tminus1010.buva.data.CategoryRepo
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.withSearchText
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryInteractor @Inject constructor(
    private val categoryRepo: CategoryRepo,
    private val categorizeTransactions: CategorizeTransactions,
) {
    val defaultFillCategory =
        categoryRepo.userCategories.map { userCategories ->
            userCategories.find { "Savings" == it.name }
                ?: userCategories.firstOrNull()
        }

    suspend fun addDescriptionAndCategorize(category: Category, description: String): Int {
        val newTransactionMatcher = category.onImportTransactionMatcher.withSearchText(description)
        categoryRepo.push(category.copy(onImportTransactionMatcher = newTransactionMatcher))
        return categorizeTransactions(newTransactionMatcher::isMatch, category::categorize)
    }
}