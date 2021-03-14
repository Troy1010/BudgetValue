package com.tminus1010.budgetvalue.layer_domain.use_cases

import com.tminus1010.budgetvalue.categoryComparator
import com.tminus1010.budgetvalue.layer_data.ActiveCategoriesDAO
import com.tminus1010.budgetvalue.model_domain.Category
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class UserCategoriesUseCasesImpl @Inject constructor(
    private val activeCategoriesDAO: ActiveCategoriesDAO
) : UserCategoriesUseCases {
    override fun push(category: Category): Completable =
        activeCategoriesDAO.push(category.toDTO())

    override fun delete(category: Category): Completable =
        activeCategoriesDAO.delete(category.toDTO())

    override fun fetchActiveCategories(): Observable<List<Category>> =
        activeCategoriesDAO.fetchActiveCategories()
            .map { it.map { Category.fromDTO(it) } }
            .map { it.sortedWith(categoryComparator) } // TODO("should be sorted later")
}