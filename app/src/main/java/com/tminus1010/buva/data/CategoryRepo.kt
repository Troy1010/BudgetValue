package com.tminus1010.buva.data

import com.tminus1010.buva.environment.room.UserCategoriesDAO
import com.tminus1010.buva.domain.Category
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepo @Inject constructor(
    private val userCategoriesDAO: UserCategoriesDAO,
) {
    val userCategories: Flow<List<Category>> =
        userCategoriesDAO.fetchUserCategories()
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)

    suspend fun push(category: Category) {
        userCategoriesDAO.push(category)
    }

    suspend fun delete(category: Category) {
        userCategoriesDAO.delete(category)
    }

    suspend fun update(category: Category) {
        userCategoriesDAO.update(category)
    }

    suspend fun hasCategory(categoryName: String): Boolean {
        return userCategoriesDAO.hasCategory(categoryName) != 0
    }
}