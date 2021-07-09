package com.tminus1010.budgetvalue.categories.data

import com.tminus1010.budgetvalue._core.data.UserCategoriesDAO
import com.tminus1010.budgetvalue.categories.models.Category
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriesRepo @Inject constructor(
    private val userCategoriesDAO: UserCategoriesDAO
) {
    val userCategories: Observable<List<Category>> =
        userCategoriesDAO.fetchUserCategories()
            .subscribeOn(Schedulers.io())
            .map { it.map { Category.fromDTO(it) } }
            .replay(1).autoConnect()

    fun push(category: Category): Completable =
        userCategoriesDAO.push(category.toDTO())
            .subscribeOn(Schedulers.io())

    fun delete(category: Category): Completable =
        userCategoriesDAO.delete(category.toDTO())
            .subscribeOn(Schedulers.io())

    fun update(category: Category): Completable =
        userCategoriesDAO.update(category.toDTO())
            .subscribeOn(Schedulers.io())

    fun hasCategory(categoryName: String): Single<Boolean> =
        userCategoriesDAO.hasCategory(categoryName)
            .map { it != 0 }
            .subscribeOn(Schedulers.io())
}