package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceArrayList
import com.tminus1010.budgetvalue.categories.models.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
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
    private val _selectedCategories = SourceArrayList<Category>()

    // # Output
    val selectedCategories = _selectedCategories.observable
        .nonLazyCache(disposables)

    val inSelectionMode: Observable<Boolean> = _selectedCategories.observable
        .map { it.isNotEmpty() }
        .distinctUntilChanged()
        .nonLazyCache(disposables)
}