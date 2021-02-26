package com.tminus1010.budgetvalue.dependency_injection

import com.tminus1010.budgetvalue.layer_ui.*

interface IDirtyInjection {
    val dirtyInjectionCache: DirtyInjectionCache
    private val component get() = dirtyInjectionCache.component

    val accountsVM: AccountsVM
        get() = dirtyInjectionCache.get { component.getAccountsVM() }

    val activePlanVM: ActivePlanVM
        get() = dirtyInjectionCache.get { component.getActivePlanVM() }

    val activeReconciliationVM: ActiveReconciliationVM
        get() = dirtyInjectionCache.get { component.getActiveReconciliationVM() }

    val activeReconciliationVM2: ActiveReconciliationVM2
        get() = dirtyInjectionCache.get { component.getActiveReconciliationVM2() }

    val advancedCategorizeVM: AdvancedCategorizeVM
        get() = dirtyInjectionCache.get { component.getAdvancedCategorizeVM() }

    val budgetedVM: BudgetedVM
        get() = dirtyInjectionCache.get { component.getBudgetedVM() }

    val categorizeVM: CategorizeVM
        get() = dirtyInjectionCache.get { component.getCategorizeVM() }

    val historyVM: HistoryVM
        get() = dirtyInjectionCache.get { component.getHistoryVM() }

    val transactionsVM: TransactionsVM
        get() = dirtyInjectionCache.get { component.getTransactionsVM() }
}