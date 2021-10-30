package com.tminus1010.budgetvalue._core.data

import androidx.room.*
import com.tminus1010.budgetvalue.categories.models.CategoryDTO
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
@Deprecated("Use UserCategoriesDAO2 for coroutines")
interface UserCategoriesDAO {
    @Query("SELECT * FROM `CategoryDTO`")
    fun fetchUserCategories(): Observable<List<CategoryDTO>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun push(category: CategoryDTO): Completable

    @Delete
    fun delete(category: CategoryDTO): Completable

    @Update
    fun update(category: CategoryDTO): Completable

    @Query("SELECT COUNT(1) FROM `CategoryDTO` WHERE name=:categoryName")
    fun hasCategory(categoryName: String): Single<Int>
}