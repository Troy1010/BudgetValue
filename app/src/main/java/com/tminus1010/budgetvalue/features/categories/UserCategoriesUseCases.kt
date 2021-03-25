package com.tminus1010.budgetvalue.features.categories

import io.reactivex.rxjava3.core.Completable

interface UserCategoriesUseCases: IUserCategoriesFetch {
    fun push(category: Category): Completable
    fun delete(category: Category): Completable
}