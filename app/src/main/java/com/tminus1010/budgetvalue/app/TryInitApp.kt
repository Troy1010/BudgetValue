package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue.data.AppInitRepo
import com.tminus1010.budgetvalue.data.CategoriesRepo
import com.tminus1010.budgetvalue.domain.AmountFormula
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue.domain.CategoryType
import java.math.BigDecimal
import javax.inject.Inject

class TryInitApp @Inject constructor(
    private val appInitRepo: AppInitRepo,
    private val categoriesRepo: CategoriesRepo,
) {
    suspend operator fun invoke() {
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