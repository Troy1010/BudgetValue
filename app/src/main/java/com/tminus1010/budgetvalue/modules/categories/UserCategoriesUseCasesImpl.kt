package com.tminus1010.budgetvalue.modules.categories

import com.tminus1010.budgetvalue.categoryComparator
import com.tminus1010.budgetvalue.layer_data.Repo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserCategoriesUseCasesImpl @Inject constructor(
    private val repo: Repo
) : UserCategoriesUseCases {
    override fun push(category: Category): Completable =
        repo.push(category.toDTO())

    override fun delete(category: Category): Completable =
        repo.delete(category.toDTO())

    override fun fetchUserCategories(): Observable<List<Category>> =
        repo.fetchUserCategories()
            .map { it.map { Category.fromDTO(it) } }
            .map { it.sortedWith(categoryComparator) } // TODO("should be sorted later")
}