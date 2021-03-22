package com.tminus1010.budgetvalue.modules.categories

import com.tminus1010.budgetvalue.modules.categories.Category
import io.reactivex.rxjava3.core.Observable

interface IUserCategoriesFetch {
    fun fetchUserCategories(): Observable<List<Category>>
}