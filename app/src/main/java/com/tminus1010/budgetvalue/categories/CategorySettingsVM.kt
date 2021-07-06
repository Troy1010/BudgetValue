package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue.categories.domain.DeleteCategoryFromActiveDomainUC
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategorySettingsVM @Inject constructor(
    private val deleteCategoryFromActiveDomainUC: DeleteCategoryFromActiveDomainUC,
) : ViewModel() {
    // # Output
    lateinit var currentCategory: Category

    // # Input
    fun deleteCurrentCategory() {
        deleteCategoryFromActiveDomainUC(currentCategory)
            .observe(disposables)
    }

    fun setup(category: Category) {
        currentCategory = category
    }
}