package com.tminus1010.buva.app

import com.tminus1010.buva.R
import com.tminus1010.buva.data.CategoryRepo
import com.tminus1010.buva.data.HasAppBeenInitializedRepo
import com.tminus1010.buva.data.SelectedBudgetHostPage
import com.tminus1010.buva.data.SelectedHostPage
import com.tminus1010.buva.domain.AmountFormula
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.ReconciliationStrategyGroup
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
import javax.inject.Inject

class InitApp @Inject constructor(
    private val hasAppBeenInitializedRepo: HasAppBeenInitializedRepo,
    private val categoryRepo: CategoryRepo,
    private val isReadyToReconcile: IsReadyToReconcile,
    private val isReadyToBudget: IsReadyToBudget,
    private val isReadyToBudgeted: IsReadyToBudgeted,
    private val selectedHostPage: SelectedHostPage,
    private val selectedBudgetHostPage: SelectedBudgetHostPage,
) {
    suspend operator fun invoke() {
        // Requirement: Given app is not readyToBudget and SelectedHostPage is Budget When user launches app Then show default page.
        if (selectedHostPage.flow.first() == R.id.budgetHostFrag && !isReadyToBudget.get())
            selectedHostPage.setDefault()
        // Requirement: Given app is not readyToReconcile and SelectedBudgetHostPage is reconcile When user launches app Then show default page.
        if ((selectedBudgetHostPage.flow.first() == R.id.reconciliationHostFrag) && !isReadyToReconcile.get())
            selectedBudgetHostPage.setDefault()
        // 
        if ((selectedBudgetHostPage.flow.first() == R.id.budgetFrag) && !isReadyToBudgeted.get())
            selectedBudgetHostPage.setDefault()
        //
        if (!hasAppBeenInitializedRepo.wasAppInitialized()) {
            initCategories.forEach { categoryRepo.push(it) }
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