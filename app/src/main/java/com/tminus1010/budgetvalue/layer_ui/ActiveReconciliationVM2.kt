package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.combineLatestAsTuple
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActiveReconciliationVM2 @Inject constructor(
    val activeReconciliationVM: ActiveReconciliationVM,
    private val budgetedVM: BudgetedVM,
) : ViewModel() {
    val defaultAmount = combineLatestAsTuple(activeReconciliationVM.caTotal, budgetedVM.defaultAmount)
        .map { it.second - it.first }
}