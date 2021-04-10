package com.tminus1010.budgetvalue.categories.data

import com.tminus1010.budgetvalue.categories.models.Category
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface ICategoriesRepo {
    fun fetchUserCategories(): Observable<List<Category>>
    fun push(category: Category): Completable
    fun delete(category: Category): Completable
}