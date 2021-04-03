package com.tminus1010.budgetvalue.categories.data

import com.tminus1010.budgetvalue.categories.Category
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

interface ICategoriesRepo {
    fun fetchUserCategories(): Observable<List<Category>>
    fun push(category: Category): Completable
    fun delete(category: Category): Completable
}