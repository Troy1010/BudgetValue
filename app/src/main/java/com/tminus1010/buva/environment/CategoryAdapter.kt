package com.tminus1010.buva.environment

import com.tminus1010.buva.all_layers.extensions.value
import com.tminus1010.buva.app.UserCategories
import com.tminus1010.buva.domain.Category
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryAdapter @Inject constructor(
    userCategories: UserCategories,
) {
    fun parseCategory(categoryName: String): Category {
        if (categoryName == Category.DEFAULT.name) error("Should never have to parse \"${Category.DEFAULT.name}\"")
        return userCategoryMap.value!![categoryName] // Using this breaks responsiveness. However, not using it means we need DTOs.. Hm.
            ?: Category.UNRECOGNIZED.also { if (categoryName != Category.UNRECOGNIZED.name) logz("Warning: returning category Unrecognized for unrecognized name:$categoryName") }
    }

    private val userCategoryMap =
        userCategories.flow
            .map { it.associate { it.name to it } }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    init {
        // TODO: Without this, userCategoryMap.value!! causes NPE. However, using .first() causes a feedback loop.
        runBlocking { userCategoryMap.take(1).collect() }
    }
}