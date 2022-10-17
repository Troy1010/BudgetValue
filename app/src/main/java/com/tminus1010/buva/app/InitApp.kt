package com.tminus1010.buva.app

import com.tminus1010.buva.data.CategoriesRepo
import com.tminus1010.buva.data.HasAppBeenInitializedRepo
import com.tminus1010.buva.domain.AmountFormula
import com.tminus1010.buva.domain.Category
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
                Category("Unknown", AmountFormula.Value(BigDecimal.ZERO), isRememberedByDefault = false),
                Category("Savings", AmountFormula.Value(BigDecimal.ZERO), resetStrategy = ResetStrategy.Basic(null)),
                Category("Emergency", AmountFormula.Value(BigDecimal.ZERO), resetStrategy = ResetStrategy.Basic(null)),
                Category("Gifts", AmountFormula.Value(BigDecimal.ZERO), resetStrategy = ResetStrategy.Basic(null)),
                Category("Activities", AmountFormula.Value(BigDecimal.ZERO), resetStrategy = ResetStrategy.Basic(null)),
            )
    }
}