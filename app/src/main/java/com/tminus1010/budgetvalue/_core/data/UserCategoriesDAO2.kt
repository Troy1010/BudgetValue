package com.tminus1010.budgetvalue._core.data

import androidx.room.*
import com.tminus1010.budgetvalue.categories.models.CategoryDTO
import kotlinx.coroutines.flow.Flow

@Dao
interface UserCategoriesDAO2 {
    @Query("SELECT * FROM `CategoryDTO`")
    fun fetchUserCategories(): Flow<List<CategoryDTO>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun push(category: CategoryDTO)

    @Delete
    suspend fun delete(category: CategoryDTO)

    @Update
    suspend fun update(category: CategoryDTO)

    @Query("SELECT COUNT(1) FROM `CategoryDTO` WHERE name=:categoryName")
    suspend fun hasCategory(categoryName: String): Int
}