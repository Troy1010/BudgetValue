package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.extensions.toLiveData
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue.categories.domain.DeleteCategoryFromActiveDomainUC
import com.tminus1010.budgetvalue.categories.models.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

@HiltViewModel
class CategorySelectionVM @Inject constructor(
    errorSubject: Subject<Throwable>,
    private val deleteCategoryFromActiveDomainUC: DeleteCategoryFromActiveDomainUC,
) : ViewModel() {
    // # Input
    fun clearSelection() = Completable.fromCallable {
        intents.onNext(Intents.ClearSelection)
    }

    fun selectCategory(category: Category) {
        intents.onNext(Intents.SelectCategory(category))
    }

    fun unselectCategory(category: Category) {
        intents.onNext(Intents.UnselectCategory(category))
    }

    fun deleteSelectedCategories() {
        state.take(1)
            .map { it.selectedCategories.map { deleteCategoryFromActiveDomainUC(it) } }
            .flatMapCompletable { Rx.merge(it) }
            .andThen(clearSelection())
            .subscribe()
    }

    // # Internal
    private sealed class Intents {
        object ClearSelection : Intents()
        class SelectCategory(val category: Category) : Intents()
        class UnselectCategory(val category: Category) : Intents()
    }

    private val intents = PublishSubject.create<Intents>()

    // # Output
    data class State(
        val selectedCategories: Set<Category> = emptySet(),
        val inSelectionMode: Boolean = false,
    )

    val state = intents
        .scan(State()) { acc, v ->
            when (v) {
                is Intents.ClearSelection -> acc.copy(
                    selectedCategories = emptySet(),
                    inSelectionMode = false
                )
                is Intents.SelectCategory -> acc.copy(
                    selectedCategories = acc.selectedCategories + v.category,
                    inSelectionMode = true
                )
                is Intents.UnselectCategory -> acc.copy(
                    selectedCategories = acc.selectedCategories - v.category,
                    inSelectionMode = (acc.selectedCategories - v.category).isNotEmpty()
                )
            }
        }
        .nonLazyCache(disposables)

    val selectedCategories: LiveData<Set<Category>> = state
        .map { it.selectedCategories }
        .distinctUntilChanged()
        .toLiveData(errorSubject)

    val inSelectionMode: Observable<Boolean> = state
        .map { it.inSelectionMode }
        .distinctUntilChanged()
        .nonLazyCache(disposables)
}