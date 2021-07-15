package com.tminus1010.budgetvalue._shared.app_init

import com.tminus1010.budgetvalue._shared.app_init.data.AppInitRepo
import com.tminus1010.budgetvalue.categories.data.CategoriesRepo
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.categories.models.CategoryType
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.tmcommonkotlin.rx.extensions.launch
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInitDomain @Inject constructor(
    private val appInitRepo: AppInitRepo,
    private val categoriesRepo: CategoriesRepo
) {
    fun appInit() {
        if (!appInitRepo.fetchAppInitBool()) {
            initCategories.forEach { categoriesRepo.push(it).launch() }
            appInitRepo.pushAppInitBool(true)
        }
    }

    companion object {
        val initCategories
            get() = listOf(
                Category("Food", CategoryType.Always, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Vanity Food", CategoryType.Reservoir, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Improvements", CategoryType.Always, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Dentist", CategoryType.Always, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Medical Supplies", CategoryType.Always, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Misc", CategoryType.Always, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Commute", CategoryType.Always, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Emergency", CategoryType.Reservoir, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Charity", CategoryType.Reservoir, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Trips", CategoryType.Reservoir, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Christmas", CategoryType.Reservoir, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Activities", CategoryType.Reservoir, AmountFormula.Value(BigDecimal.ZERO)),
            )
    }
}