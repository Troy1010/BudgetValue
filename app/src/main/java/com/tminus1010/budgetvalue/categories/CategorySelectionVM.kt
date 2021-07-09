package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.divertErrors
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.doLogx
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

@HiltViewModel
class CategorySelectionVM @Inject constructor(
    errorSubject: Subject<Throwable>,
) : ViewModel() {
    // # Input
    fun clearSelection(): Completable = Completable.fromCallable {
        intents.onNext(Intents.ClearSelection)
    }

    fun selectCategories(vararg categories: Category) {
        categories.forEach { intents.onNext(Intents.SelectCategory(it)) }
    }

    fun unselectCategories(vararg categories: Category) {
        categories.forEach { intents.onNext(Intents.UnselectCategory(it)) }
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
        .doLogx("state")
        .nonLazyCache(disposables)

    val selectedCategories = state
        .map { it.selectedCategories }
        .distinctUntilChanged()
        .divertErrors(errorSubject)

    val inSelectionMode: Observable<Boolean> = state
        .map { it.inSelectionMode }
        .distinctUntilChanged()
        .doLogx("inSelectionMode")
        .nonLazyCache(disposables)
}