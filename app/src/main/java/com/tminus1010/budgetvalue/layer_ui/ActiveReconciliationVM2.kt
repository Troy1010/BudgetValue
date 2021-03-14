package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.combineLatestAsTuple
import com.tminus1010.budgetvalue.layer_domain.Domain

// Separate from ActiveReconciliationVM to avoid circular dependency graph
class ActiveReconciliationVM2(
    activeReconciliationVM: ActiveReconciliationVM,
    budgetedVM: BudgetedVM,
    domain: Domain,
    transactionsVM: TransactionsVM,
) : ViewModel() {
    // This calculation is a bit confusing. Take a look at ManualCalculationsForTests for clarification
    val defaultAmount =
        combineLatestAsTuple(
            domain.plans,
            domain.reconciliations,
            transactionsVM.transactionBlocks,
            activeReconciliationVM.caTotal,
            budgetedVM.defaultAmount
        )
            .map { (plans, reconciliations, transactionBlocks, activeReconciliationCATotal, budgetedDefaultAmount) ->
                (plans.map { it.defaultAmount } +
                        reconciliations.map { it.defaultAmount } +
                        transactionBlocks.map { it.defaultAmount } +
                        activeReconciliationCATotal)
                    .fold(0.toBigDecimal()) { acc, v -> acc + v }
                    .let { budgetedDefaultAmount - it }
            }
}