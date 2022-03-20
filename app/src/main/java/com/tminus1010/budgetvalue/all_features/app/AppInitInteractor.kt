package com.tminus1010.budgetvalue.all_features.app

import com.tminus1010.budgetvalue.all_features.data.repo.AppInitRepo
import com.tminus1010.budgetvalue.categories.data.CategoriesRepo
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.categories.models.CategoryType
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import java.math.BigDecimal
import javax.inject.Inject

class AppInitInteractor @Inject constructor(
    private val appInitRepo: AppInitRepo,
    private val categoriesRepo: CategoriesRepo,
) {
    suspend fun tryInitializeApp() {
        if (!appInitRepo.isAppInitialized()) {
            initCategories.forEach { categoriesRepo.push(it) }
            appInitRepo.pushAppInitBool2(true)
        }
    }

    companion object {
        val initCategories
            get() = listOf(
                Category("Food", CategoryType.Always, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Vanity Food", CategoryType.Reservoir, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Rent", CategoryType.Always, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Improvements", CategoryType.Always, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Dentist", CategoryType.Always, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Medical Supplies", CategoryType.Always, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Misc", CategoryType.Always, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Commute", CategoryType.Always, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Emergency", CategoryType.Reservoir, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Charity", CategoryType.Reservoir, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Trips", CategoryType.Reservoir, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Gifts", CategoryType.Reservoir, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Activities", CategoryType.Reservoir, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Haircuts", CategoryType.Reservoir, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Unknown", CategoryType.Always, AmountFormula.Value(BigDecimal.ZERO)),
            )
    }
}