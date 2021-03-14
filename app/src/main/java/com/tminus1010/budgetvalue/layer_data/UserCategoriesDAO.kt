package com.tminus1010.budgetvalue.layer_data

import androidx.room.*
import com.tminus1010.budgetvalue.model_data.CategoryDTO
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

@Dao
interface UserCategoriesDAO {
    @Query("select * from `CategoryDTO`")
    fun fetchActiveCategories(): Observable<List<CategoryDTO>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun push(category: CategoryDTO): Completable

    @Delete
    fun delete(category: CategoryDTO): Completable

    @Update
    fun update(category: CategoryDTO): Completable
}