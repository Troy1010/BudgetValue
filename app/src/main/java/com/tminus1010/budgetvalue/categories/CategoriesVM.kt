package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.extensions.divertErrors
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.models.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

@HiltViewModel
class CategoriesVM @Inject constructor(
    errorSubject: Subject<Throwable>,
    categoriesDomain: CategoriesDomain,
) : ViewModel() {
    // # State
    val userCategories: Observable<List<Category>> = categoriesDomain.userCategories
        .divertErrors(errorSubject)
}