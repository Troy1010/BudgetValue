package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.extensions.onIO
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.model_data.Category
import javax.inject.Inject

class AppInitializer @Inject constructor(
    val repo: Repo,
) : IAppInitializer {
    override fun appInit() {
        if (!repo.fetchAppInitBool()) {
            initCategories
                .forEach { repo.push(it).onIO() }
            repo.pushAppInitBool()
        }
    }

    val initCategories
        get() = listOf(
            Category("Food", Category.Type.Always),
            Category("Drinks", Category.Type.Always),
            Category("Improvements", Category.Type.Always),
            Category("Dentist", Category.Type.Always),
            Category("Medical Supplies", Category.Type.Always),
            Category("Leli gifts/activities", Category.Type.Always),
            Category("Misc", Category.Type.Always),
            Category("Gas", Category.Type.Always),
            Category("Vanity Food", Category.Type.Reservoir),
            Category("Emergency", Category.Type.Reservoir),
            Category("Charity", Category.Type.Reservoir),
            Category("Trips", Category.Type.Reservoir),
            Category("Christmas", Category.Type.Reservoir),
            Category("Gifts", Category.Type.Reservoir),
            Category("Activities", Category.Type.Reservoir),
        )
}