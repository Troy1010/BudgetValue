package com.tminus1010.buva.ui.all_features

import androidx.navigation.NavController
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.ui.category_details.CategoryDetailsFrag
import dagger.Reusable
import javax.inject.Inject

@Reusable
class AndroidNavigationWrapper @Inject constructor() : IAndroidNavigationWrapper {
    private val nav get() = Companion.nav!!
    override fun navToCreateCategory() {
        CategoryDetailsFrag.navTo(nav, null)
    }

    override fun navToEditCategory(category: Category) {
        CategoryDetailsFrag.navTo(nav, category)
    }

    companion object {
        var nav: NavController? = null
    }
}