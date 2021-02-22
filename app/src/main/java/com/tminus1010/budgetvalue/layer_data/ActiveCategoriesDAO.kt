package com.tminus1010.budgetvalue.layer_data

import androidx.room.*
import com.tminus1010.budgetvalue.model_data.Category
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

@Dao
interface ActiveCategoriesDAO {
    @Query("select * from `Category`")
    fun fetchActiveCategories(): Observable<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun push(category: Category): Completable

    @Delete
    fun delete(category: Category): Completable

    @Update
    fun update(category: Category): Completable
}