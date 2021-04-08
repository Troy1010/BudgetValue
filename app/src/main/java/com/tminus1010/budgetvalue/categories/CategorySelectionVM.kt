package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.middleware.AddRemType
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.domain.DeleteCategoryFromActiveDomainUC
import com.tminus1010.budgetvalue.categories.domain.ICategoriesDomain
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class CategorySelectionVM @Inject constructor(
    categoriesDomain: CategoriesDomain,
    private val deleteCategoryFromActiveDomainUC: DeleteCategoryFromActiveDomainUC
) : ViewModel(), ICategoriesDomain by categoriesDomain {
    // # Buses
    private val intentSelectCategoryBus = PublishSubject.create<Pair<AddRemType, Category>>()
    private val intentClearSelectionBus = PublishSubject.create<Unit>()

    // # Databinding Sources
    val selectedCategories: BehaviorSubject<Set<Category>> =
        Rx.merge(intentSelectCategoryBus, intentClearSelectionBus)
            .scan(setOf<Category>()) { acc, (v1, v2) ->
                when {
                    v1 != null -> when (v1.first) {
                        AddRemType.ADD -> acc.plus(v1.second)
                        AddRemType.REMOVE -> acc.minus(v1.second)
                    }
                    v2 != null -> emptySet()
                    else -> error("How did we get here?")
                }
            }
            .toBehaviorSubject()
    val inSelectionMode : BehaviorSubject<Boolean> = selectedCategories
        .map { it.isNotEmpty() }
        .toBehaviorSubject()

    // # User Intents
    fun clearSelection() {
        intentClearSelectionBus.onNext(Unit)
    }
    fun selectCategory(addRemType: AddRemType, category: Category) {
        intentSelectCategoryBus.onNext(Pair(addRemType, category))
    }
    fun deleteSelectedCategories() {
        selectedCategories.take(1)
            .flatMapCompletable { Rx.merge(it.map { deleteCategoryFromActiveDomainUC(it) }) }
            .subscribe()
        clearSelection()
    }
}