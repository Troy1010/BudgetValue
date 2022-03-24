package com.tminus1010.budgetvalue.data.service

import androidx.room.*
import com.tminus1010.budgetvalue.domain.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface UserCategoriesDAO {
    @Query("SELECT * FROM `Category`")
    fun fetchUserCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun push(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Update
    suspend fun update(category: Category)

    @Query("SELECT COUNT(1) FROM `Category` WHERE name=:categoryName")
    suspend fun hasCategory(categoryName: String): Int
}