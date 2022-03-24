package com.tminus1010.budgetvalue._unrestructured.categories

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.ui.errors.Errors
import com.tminus1010.budgetvalue._unrestructured.categories.domain.CategoriesInteractor
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