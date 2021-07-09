package com.tminus1010.budgetvalue._shared.app_init

import com.tminus1010.budgetvalue._shared.app_init.data.AppInitRepo
import com.tminus1010.budgetvalue.categories.data.CategoriesRepo
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.categories.models.CategoryType
import com.tminus1010.tmcommonkotlin.rx.extensions.launch
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInitDomain @Inject constructor(
    private val appInitRepo: AppInitRepo,
    private val categoriesRepo: CategoriesRepo
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
                Category("Food", CategoryType.Always, BigDecimal.ZERO),
                Category("Vanity Food", CategoryType.Reservoir, BigDecimal.ZERO),
                Category("Improvements", CategoryType.Always, BigDecimal.ZERO),
                Category("Dentist", CategoryType.Always, BigDecimal.ZERO),
                Category("Medical Supplies", CategoryType.Always, BigDecimal.ZERO),
                Category("Misc", CategoryType.Always, BigDecimal.ZERO),
                Category("Commute", CategoryType.Always, BigDecimal.ZERO),
                Category("Emergency", CategoryType.Reservoir, BigDecimal.ZERO),
                Category("Charity", CategoryType.Reservoir, BigDecimal.ZERO),
                Category("Trips", CategoryType.Reservoir, BigDecimal.ZERO),
                Category("Christmas", CategoryType.Reservoir, BigDecimal.ZERO),
                Category("Activities", CategoryType.Reservoir, BigDecimal.ZERO),
            )
    }
}