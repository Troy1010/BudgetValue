package com.tminus1010.budgetvalue.layer_domain.use_cases

import com.tminus1010.budgetvalue.model_domain.Category
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface UserCategoriesUseCases {
    fun fetchUserCategories(): Observable<List<Category>>
    fun push(category: Category): Completable
    fun delete(category: Category): Completable
}