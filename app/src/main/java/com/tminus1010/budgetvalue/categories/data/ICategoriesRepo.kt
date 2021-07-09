package com.tminus1010.budgetvalue.categories.data

import com.tminus1010.budgetvalue.categories.models.Category
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface ICategoriesRepo {
    val userCategories: Observable<List<Category>>
    fun push(category: Category): Completable
    fun delete(category: Category): Completable
    fun update(category: Category): Completable
    fun hasCategory(categoryName: String): Single<Boolean>
}