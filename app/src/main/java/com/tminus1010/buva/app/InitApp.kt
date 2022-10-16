package com.tminus1010.buva.app

import com.tminus1010.buva.data.CategoriesRepo
import com.tminus1010.buva.data.HasAppBeenInitializedRepo
import com.tminus1010.buva.domain.AmountFormula
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.CategoryType
import java.math.BigDecimal
import javax.inject.Inject

class InitApp @Inject constructor(
    private val hasAppBeenInitializedRepo: HasAppBeenInitializedRepo,
    private val categoriesRepo: CategoriesRepo,
) {
    suspend operator fun invoke() {
        if (!hasAppBeenInitializedRepo.wasAppInitialized()) {
            initCategories.forEach { categoriesRepo.push(it) }
            hasAppBeenInitializedRepo.pushAppInitBool2(true)
        }
    }

    companion object {
        val initCategories
            get() = listOf(
                Category("Rent", CategoryType.Always, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Improvements", CategoryType.Always, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Medical", CategoryType.Always, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Misc", CategoryType.Always, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Savings", CategoryType.Reservoir, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Emergency", CategoryType.Reservoir, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Gifts", CategoryType.Reservoir, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Activities", CategoryType.Reservoir, AmountFormula.Value(BigDecimal.ZERO)),
                Category("Unknown", CategoryType.Always, AmountFormula.Value(BigDecimal.ZERO), isRememberedByDefault = false),
            )
    }
}