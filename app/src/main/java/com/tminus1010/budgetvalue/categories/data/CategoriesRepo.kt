package com.tminus1010.budgetvalue.categories.data

import com.tminus1010.budgetvalue._core.data.UserCategoriesDAO
import com.tminus1010.budgetvalue.categories.models.Category
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriesRepo @Inject constructor(
    private val userCategoriesDAO: UserCategoriesDAO
) : ICategoriesRepo {
    override fun fetchUserCategories(): Observable<List<Category>> =
        userCategoriesDAO.fetchUserCategories().map { it.map { Category.fromDTO(it) } }
            .subscribeOn(Schedulers.io())

    override fun push(category: Category): Completable =
        userCategoriesDAO.push(category.toDTO())
            .subscribeOn(Schedulers.io())

    override fun delete(category: Category): Completable =
        userCategoriesDAO.delete(category.toDTO())
            .subscribeOn(Schedulers.io())

    override fun update(category: Category): Completable =
        userCategoriesDAO.update(category.toDTO())
            .subscribeOn(Schedulers.io())
}