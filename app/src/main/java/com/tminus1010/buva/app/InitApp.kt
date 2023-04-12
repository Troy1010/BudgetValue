package com.tminus1010.buva.app

import com.tminus1010.buva.data.CategoriesRepo
import com.tminus1010.buva.data.HasAppBeenInitializedRepo
import com.tminus1010.buva.domain.AmountFormula
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.ReconciliationStrategyGroup
import com.tminus1010.buva.domain.ResetStrategy
import java.math.BigDecimal
import javax.inject.Inject

class InitApp @Inject constructor(
    private val hasAppBeenInitializedRepo: HasAppBeenInitializedRepo,
    private val categoriesRepo: CategoriesRepo,
) {
    suspend operator fun invoke() {
        if (!hasAppBeenInitializedRepo.wasAppInitialized()) {
            initCategories.forEach { categoriesRepo.push(it) }
            hasAppBeenInitializedRepo.pushAppInitBool(true)
        }
    }

    companion object {
        val initCategories
            get() = listOf(
                Category("Rent", AmountFormula.Value(BigDecimal.ZERO)),
                Category("Improvements", AmountFormula.Value(BigDecimal.ZERO)),
                Category("Medical", AmountFormula.Value(BigDecimal.ZERO)),
                Category("Misc", AmountFormula.Value(BigDecimal.ZERO)),
                Category("Food", AmountFormula.Value(BigDecimal.ZERO), reconciliationStrategyGroup = ReconciliationStrategyGroup.Reservoir(ResetStrategy.Basic(0))),
                Category("Unknown", AmountFormula.Value(BigDecimal.ZERO), isRememberedByDefault = false, reconciliationStrategyGroup = ReconciliationStrategyGroup.Reservoir(ResetStrategy.Basic(0))),
                Category("Savings", AmountFormula.Value(BigDecimal.ZERO)),
                Category("Emergency", AmountFormula.Value(BigDecimal.ZERO)),
                Category("Gifts", AmountFormula.Value(BigDecimal.ZERO)),
                Category("Activities", AmountFormula.Value(BigDecimal.ZERO)),
            )
    }
}