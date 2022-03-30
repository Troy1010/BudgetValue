package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue.all_layers.extensions.value
import com.tminus1010.budgetvalue.data.CategoriesRepo
import com.tminus1010.budgetvalue.domain.Category
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryParser @Inject constructor(
    private val categoriesRepo: CategoriesRepo,
) {
    fun parseCategory(categoryName: String): Category {
        if (categoryName == Category.DEFAULT.name) error("Should never have to parse \"${Category.DEFAULT.name}\"")
        return userCategoryMap.value!![categoryName]
            ?: Category.UNRECOGNIZED.also { logz("Warning: returning category Unrecognized for unrecognized name:$categoryName") }
    }

    val userCategories =
        categoriesRepo.userCategories
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    private val userCategoryMap =
        userCategories
            .map { it.associate { it.name to it } }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)
}