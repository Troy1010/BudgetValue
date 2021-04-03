package com.tminus1010.budgetvalue.categories

import com.tminus1010.budgetvalue._core.data.RepoFacade
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserCategoriesUseCasesImpl @Inject constructor(
    private val repoFacade: RepoFacade
) : UserCategoriesUseCases {
    override fun push(category: Category): Completable =
        repoFacade.push(category.toDTO())

    override fun delete(category: Category): Completable =
        repoFacade.delete(category.toDTO())

    override fun fetchUserCategories(): Observable<List<Category>> =
        repoFacade.fetchUserCategories()
            .map { it.map { Category.fromDTO(it) } }
}