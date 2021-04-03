package com.tminus1010.budgetvalue._core.shared_features.app_init

import com.tminus1010.budgetvalue._core.shared_features.app_init.data.IAppInitRepo
import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.categories.data.ICategoriesRepo
import com.tminus1010.tmcommonkotlin.rx.extensions.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInitDomain @Inject constructor(
    private val appInitRepo: IAppInitRepo,
    private val categoriesRepo: ICategoriesRepo
) : IAppInitializer {
    override fun appInit() {
        if (!appInitRepo.fetchAppInitBool()) {
            initCategories.forEach { categoriesRepo.push(it).launch() }
            appInitRepo.pushAppInitBool(true)
        }
    }

    companion object {
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
}