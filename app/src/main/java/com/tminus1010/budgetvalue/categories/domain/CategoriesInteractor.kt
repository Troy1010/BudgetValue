package com.tminus1010.budgetvalue.categories.domain

import com.tminus1010.budgetvalue._core.categoryComparator
import com.tminus1010.budgetvalue.categories.data.CategoriesRepo
import com.tminus1010.budgetvalue.categories.models.Category
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx3.asObservable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriesInteractor @Inject constructor(
    categoriesRepo: CategoriesRepo,
) {
    fun parseCategory(categoryName: String): Category {
        if (categoryName == Category.DEFAULT.name) error("Should never have to parse \"${Category.DEFAULT.name}\"")
        return nameToCategoryMap.blockingFirst()[categoryName]
            ?: Category.UNRECOGNIZED.also { logz("Warning: returning category Unrecognized for unrecognized name:$categoryName") }
    }

    val userCategoriesFlow = categoriesRepo.userCategories.map { it.sortedWith(categoryComparator) }

    @Deprecated("use userCategoriesFlow")
    val userCategories = userCategoriesFlow.asObservable()
    private val nameToCategoryMap =
        userCategories
            .map { it.associateBy { it.name } as HashMap<String, Category> }
            .replay(1).apply { connect() }
}