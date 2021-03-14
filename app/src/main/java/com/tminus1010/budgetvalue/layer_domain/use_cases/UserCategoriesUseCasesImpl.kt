package com.tminus1010.budgetvalue.layer_domain.use_cases

import com.tminus1010.budgetvalue.categoryComparator
import com.tminus1010.budgetvalue.layer_data.ActiveCategoriesDAO
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.model_domain.Category
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class UserCategoriesUseCasesImpl @Inject constructor(
    private val repo: Repo
) : UserCategoriesUseCases {
    override fun push(category: Category): Completable =
        repo.push(category.toDTO())

    override fun delete(category: Category): Completable =
        repo.delete(category.toDTO())

    override fun fetchActiveCategories(): Observable<List<Category>> =
        repo.fetchActiveCategories()
            .map { it.map { Category.fromDTO(it) } }
            .map { it.sortedWith(categoryComparator) } // TODO("should be sorted later")
}