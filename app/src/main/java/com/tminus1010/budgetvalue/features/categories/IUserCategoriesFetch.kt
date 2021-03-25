package com.tminus1010.budgetvalue.features.categories

import io.reactivex.rxjava3.core.Observable

interface IUserCategoriesFetch {
    fun fetchUserCategories(): Observable<List<Category>>
}