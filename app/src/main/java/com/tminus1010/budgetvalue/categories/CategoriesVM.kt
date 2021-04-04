package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.middleware.AddRemType
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain2
import com.tminus1010.budgetvalue.categories.domain.ICategoriesDomain
import com.tminus1010.budgetvalue.categories.domain.ICategoriesDomain2
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class CategoriesVM @Inject constructor(
    categoriesDomain: CategoriesDomain,
    categoriesDomain2: CategoriesDomain2
) : ViewModel(),
    ICategoriesDomain by categoriesDomain,
    ICategoriesDomain2 by categoriesDomain2 {
    // # Buses
    private val intentSelectCategoryBus = PublishSubject.create<Pair<AddRemType, Category>>()

    // # Databinding Sources
    val selectedCategories : BehaviorSubject<Set<Category>> =
        intentSelectCategoryBus.scan(setOf<Category>()) { acc, (addRemType, category) ->
            when(addRemType) {
                AddRemType.ADD -> acc.plus(category)
                AddRemType.REMOVE -> acc.minus(category)
            }
        }.toBehaviorSubject()

    // # Intents
    fun selectCategory(addRemType: AddRemType, category: Category) {
        intentSelectCategoryBus.onNext(Pair(addRemType, category))
    }
    fun deleteSelectedCategories() {
        selectedCategories.take(1)
            .doOnNext { it.map { intentDeleteCategoryFromActive.onNext(it) } }
            .subscribe()
    }
}