package com.tminus1010.budgetvalue.categories.data

import com.tminus1010.budgetvalue._core.data.CategoryDatabase
import com.tminus1010.budgetvalue.categories.models.Category
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriesRepo @Inject constructor(
    categoryDatabase: CategoryDatabase,
) {
    private val userCategoriesDAO2 = categoryDatabase.userCategoriesDAO2()

    val userCategories: Flow<List<Category>> =
        userCategoriesDAO2.fetchUserCategories()
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)

    suspend fun push(category: Category) {
        userCategoriesDAO2.push(category)
    }

    suspend fun delete(category: Category) {
        userCategoriesDAO2.delete(category)
    }

    suspend fun update(category: Category) {
        userCategoriesDAO2.update(category)
    }

    suspend fun hasCategory(categoryName: String): Boolean {
        return userCategoriesDAO2.hasCategory(categoryName) != 0
    }
}