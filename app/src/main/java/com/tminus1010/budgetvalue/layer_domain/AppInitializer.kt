package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.model_domain.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.launch
import javax.inject.Inject

class AppInitializer @Inject constructor(
    val repoWrapper: RepoWrapper,
    val activeCategoriesDAOWrapper: ActiveCategoriesDAOWrapper,
) : IAppInitializer {
    override fun appInit() {
        if (!repoWrapper.fetchAppInitBool()) {
            initCategories
                .forEach { activeCategoriesDAOWrapper.push(it).launch() }
            repoWrapper.pushAppInitBool()
        }
    }

    val initCategories
        get() = listOf(
            Category("Food", Category.Type.Always),
            Category("Vanity Food", Category.Type.Reservoir),
            Category("Improvements", Category.Type.Always),
            Category("Dentist", Category.Type.Always),
            Category("Medical Supplies", Category.Type.Always),
            Category("Misc", Category.Type.Always),
            Category("Commute", Category.Type.Always),
            Category("Emergency", Category.Type.Reservoir),
            Category("Charity", Category.Type.Reservoir),
            Category("Trips", Category.Type.Reservoir),
            Category("Christmas", Category.Type.Reservoir),
            Category("Activities", Category.Type.Reservoir),
        )
}