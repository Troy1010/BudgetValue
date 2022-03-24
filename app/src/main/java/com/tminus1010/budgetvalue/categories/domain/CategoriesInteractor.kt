package com.tminus1010.budgetvalue.categories.domain

import com.tminus1010.budgetvalue.all_features.data.CategoriesRepo
import com.tminus1010.budgetvalue.all_features.app.model.Category
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriesInteractor @Inject constructor(
    categoriesRepo: CategoriesRepo,
) {
    fun parseCategory(categoryName: String): Category {
        if (categoryName == Category.DEFAULT.name) error("Should never have to parse \"${Category.DEFAULT.name}\"")
        return userCategoryMap.value[categoryName]
            ?: Category.UNRECOGNIZED.also { logz("Warning: returning category Unrecognized for unrecognized name:$categoryName") }
    }

    val userCategories =
        categoriesRepo.userCategories
            .stateIn(GlobalScope, SharingStarted.Eagerly, emptyList())

    private val userCategoryMap =
        userCategories
            .map { it.associate { it.name to it } }
            .stateIn(GlobalScope, SharingStarted.Eagerly, emptyMap())
}