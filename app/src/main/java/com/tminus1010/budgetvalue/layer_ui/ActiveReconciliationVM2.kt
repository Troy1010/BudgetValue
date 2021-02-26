package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.combineLatestAsTuple
import javax.inject.Inject
import javax.inject.Singleton

// Must be separate from ActiveReconciliationVM to avoid circular dependency graph
class ActiveReconciliationVM2 constructor(
    activeReconciliationVM: ActiveReconciliationVM,
    budgetedVM: BudgetedVM,
) : ViewModel() {
    val defaultAmount = combineLatestAsTuple(activeReconciliationVM.caTotal, budgetedVM.defaultAmount)
        .map { it.second - it.first }
}