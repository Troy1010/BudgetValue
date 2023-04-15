package com.tminus1010.buva.app

import com.tminus1010.buva.R
import com.tminus1010.buva.data.CategoriesRepo
import com.tminus1010.buva.data.HasAppBeenInitializedRepo
import com.tminus1010.buva.data.SelectedHostPage
import com.tminus1010.buva.domain.AmountFormula
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.ReconciliationStrategyGroup
import com.tminus1010.buva.ui.all_features.ReadyToBudgetPresentationFactory
import com.tminus1010.buva.ui.all_features.isReady
import java.math.BigDecimal
import javax.inject.Inject

class InitApp @Inject constructor(
    private val hasAppBeenInitializedRepo: HasAppBeenInitializedRepo,
    private val categoriesRepo: CategoriesRepo,
    private val readyToBudgetPresentationFactory: ReadyToBudgetPresentationFactory,
    private val selectedHostPage: SelectedHostPage,
) {
    suspend operator fun invoke() {
        // Requirement: Given app is not readyToBudget and SelectedHostPage is Budget When user launches app Then show default page.
        if (selectedHostPage.flow.value == R.id.budgetHostFrag && !readyToBudgetPresentationFactory.isReady())
            selectedHostPage.setDefault().logx("selectedHostPage.setDefault")
        //
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
                Category("Misc", AmountFormula.Value(BigDecimal.ZERO), isRememberedWithEditByDefault = false),
                Category("Food", AmountFormula.Value(BigDecimal.ZERO), reconciliationStrategyGroup = ReconciliationStrategyGroup.Always),
                Category("Unknown", AmountFormula.Value(BigDecimal.ZERO), isRememberedWithEditByDefault = false),
                Category("Savings", AmountFormula.Value(BigDecimal.ZERO)),
                Category("Emergency", AmountFormula.Value(BigDecimal.ZERO)),
                Category("Gifts", AmountFormula.Value(BigDecimal.ZERO)),
                Category("Activities", AmountFormula.Value(BigDecimal.ZERO)),
                Category("Dates", AmountFormula.Value(BigDecimal.ZERO)),
                Category("Games", AmountFormula.Value(BigDecimal.ZERO)),
            )
    }
}