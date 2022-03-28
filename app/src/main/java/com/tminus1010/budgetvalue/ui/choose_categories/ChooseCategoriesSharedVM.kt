package com.tminus1010.budgetvalue.ui.choose_categories

import com.tminus1010.budgetvalue.framework.source_objects.SourceList
import com.tminus1010.budgetvalue.domain.Category
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

// TODO: Where should this cache exist..? How can it best be shared..?
@Singleton
class ChooseCategoriesSharedVM @Inject constructor() {
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
    private val _selectedCategories = SourceList<Category>()

    // # Output
    val selectedCategories =
        _selectedCategories.flow
            // Sadly, stateIn forces .distinctUntilChanged(). This is a workaround.
            .map { object : List<Category> by it {} }
            .stateIn(GlobalScope, SharingStarted.Eagerly, listOf())
}