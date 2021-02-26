package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.model_data.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.launch
import javax.inject.Inject

class AppInitializer @Inject constructor(
    val repo: Repo,
) : IAppInitializer {
    override fun appInit() {
        if (!repo.fetchAppInitBool()) {
            initCategories
                .forEach { repo.push(it).launch() }
            repo.pushAppInitBool()
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