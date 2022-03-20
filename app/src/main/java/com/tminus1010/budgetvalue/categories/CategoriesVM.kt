package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.all_features.presentation.Errors
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.tmcommonkotlin.coroutines.extensions.divertErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategoriesVM @Inject constructor(
    errors: Errors,
    categoriesInteractor: CategoriesInteractor,
) : ViewModel() {
    // # State
    val userCategories =
        categoriesInteractor.userCategories
            .divertErrors(errors)
}