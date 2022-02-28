package com.tminus1010.budgetvalue.replay_or_future.app

import com.tminus1010.budgetvalue._core.framework.source_objects.SourceArrayList
import com.tminus1010.budgetvalue.categories.models.Category
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectCategoriesModel @Inject constructor() {
    // # Input
    suspend fun clearSelection() {
        _selectedCategories.clear()
    }

    fun selectCategories(vararg categories: Category) {
        _selectedCategories.addAll(categories)
    }

    fun unselectCategories(vararg categories: Category) {
        _selectedCategories.removeAll(categories)
    }

    // # Internal
    private val _selectedCategories = SourceArrayList<Category>()

    // # Output
    val selectedCategories =
        _selectedCategories.flow
            // Sadly, stateIn forces .distinctUntilChanged(). This is a workaround.
            .map { object : List<Category> by it {} }
            .stateIn(GlobalScope, SharingStarted.Eagerly, listOf())
}