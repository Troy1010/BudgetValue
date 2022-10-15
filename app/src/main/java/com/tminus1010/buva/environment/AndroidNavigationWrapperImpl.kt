package com.tminus1010.buva.environment

import androidx.navigation.NavController
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.ui.category_details.CategoryDetailsFrag
import dagger.Reusable
import javax.inject.Inject

@Reusable
class AndroidNavigationWrapperImpl @Inject constructor() : AndroidNavigationWrapper {
    private val nav get() = Companion.nav ?: error("This class expects that Companion.nav is assigned")
    override fun navToCreateCategory() {
        CategoryDetailsFrag.navTo(nav, null)
    }

    override fun navToEditCategory(category: Category) {
        CategoryDetailsFrag.navTo(nav, category)
    }

    companion object {
        // This pattern can cause memory leaks. However, this project only has 1 NavController, so a memory leak is unlikely
        var nav: NavController? = null
    }
}