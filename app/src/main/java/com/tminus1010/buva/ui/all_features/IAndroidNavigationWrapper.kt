package com.tminus1010.buva.ui.all_features

import com.tminus1010.buva.domain.Category

interface IAndroidNavigationWrapper {
    fun navToCreateCategory()
    fun navToEditCategory(category: Category)
}