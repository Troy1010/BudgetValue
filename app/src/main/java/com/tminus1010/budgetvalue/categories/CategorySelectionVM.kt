package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.framework.source_objects.SourceList
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.tmcommonkotlin.rx.nonLazy
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

@HiltViewModel
class CategorySelectionVM @Inject constructor() : ViewModel() {
    // # Input
    fun clearSelection(): Completable = Completable.fromCallable {
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
    val selectedCategories = _selectedCategories.observable
        .replayNonError(1).nonLazy(disposables)
}