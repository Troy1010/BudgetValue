package com.tminus1010.budgetvalue.layer_data

import androidx.room.*
import com.tminus1010.budgetvalue.model_data.Category
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

@Dao
interface ActiveCategoryDAO {
    @Query("select * from `Category`")
    fun fetchActiveCategories(): Observable<List<Category>>

    @Query("select * from `Category` where name=:name")
    fun fetchActiveCategory(name: String): Observable<Category>

    @Insert
    fun add(category: Category): Completable

    @Delete
    fun delete(category: Category): Completable

    @Update
    fun update(category: Category): Completable
}