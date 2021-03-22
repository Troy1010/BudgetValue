package com.tminus1010.budgetvalue.modules_shared

import com.tminus1010.budgetvalue.modules.categories.UserCategoriesUseCasesImpl
import com.tminus1010.budgetvalue.modules.categories.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.launch
import javax.inject.Inject

class AppInitializer @Inject constructor(
    private val appInitBoolUseCasesImpl: AppInitBoolUseCasesImpl,
    private val userCategoriesUseCasesImpl: UserCategoriesUseCasesImpl,
) : IAppInitializer {
    override fun appInit() {
        if (!appInitBoolUseCasesImpl.fetchAppInitBool()) {
            initCategories
                .forEach { userCategoriesUseCasesImpl.push(it).launch() }
            appInitBoolUseCasesImpl.pushAppInitBool()
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