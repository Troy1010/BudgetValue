package com.tminus1010.budgetvalue.layer_domain.use_cases

import com.tminus1010.budgetvalue.model_domain.Category
import io.reactivex.rxjava3.core.Completable

interface UserCategoriesUseCases: IUserCategoriesFetch {
    fun push(category: Category): Completable
    fun delete(category: Category): Completable
}