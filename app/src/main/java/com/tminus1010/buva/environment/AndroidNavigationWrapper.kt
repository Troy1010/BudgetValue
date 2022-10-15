package com.tminus1010.buva.environment

import com.tminus1010.buva.domain.Category

interface AndroidNavigationWrapper {
    fun navToCreateCategory()
    fun navToEditCategory(category: Category)
}