package com.tminus1010.budgetvalue.replay_or_future.presentation

import com.tminus1010.budgetvalue._core.framework.source_objects.SourceArrayList
import com.tminus1010.budgetvalue.categories.models.Category
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectedCategoriesSharedVM @Inject constructor() {
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
            .stateIn(GlobalScope, SharingStarted.Eagerly, listOf())

    val inSelectionMode =
        _selectedCategories.flow
            .map { it.isNotEmpty() }
            .distinctUntilChanged()
            .stateIn(GlobalScope, SharingStarted.Eagerly, false)
}