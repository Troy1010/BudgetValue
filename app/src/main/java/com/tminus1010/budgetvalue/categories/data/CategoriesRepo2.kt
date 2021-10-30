package com.tminus1010.budgetvalue.categories.data

import com.tminus1010.budgetvalue._core.data.MiscDatabase
import com.tminus1010.budgetvalue._core.data.UserCategoriesDAO2
import com.tminus1010.budgetvalue.categories.models.Category
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriesRepo2 constructor(
    private val userCategoriesDAO2: UserCategoriesDAO2
) {
    @Inject
    constructor(miscDatabase: MiscDatabase) : this(miscDatabase.userCategoriesDAO2())

    val userCategories: Flow<List<Category>> =
        userCategoriesDAO2.fetchUserCategories()
            .map { it.map { Category.fromDTO(it) } }
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)

    suspend fun push(category: Category) {
        userCategoriesDAO2.push(category.toDTO())
    }

    suspend fun delete(category: Category) {
        userCategoriesDAO2.delete(category.toDTO())
    }

    suspend fun update(category: Category) {
        userCategoriesDAO2.update(category.toDTO())
    }

    suspend fun hasCategory(categoryName: String): Boolean {
        return userCategoriesDAO2.hasCategory(categoryName) != 0
    }
}