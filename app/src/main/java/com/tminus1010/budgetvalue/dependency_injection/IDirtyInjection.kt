package com.tminus1010.budgetvalue.dependency_injection

import com.tminus1010.budgetvalue.layer_ui.*

interface IDirtyInjection {
    val dirtyInjectionCache: DirtyInjectionCache

    val accountsVM: AccountsVM
        get() = dirtyInjectionCache.get()

    val activePlanVM: ActivePlanVM
        get() = dirtyInjectionCache.get()

    val activeReconciliationVM: ActiveReconciliationVM
        get() = dirtyInjectionCache.get()

    val activeReconciliationVM2: ActiveReconciliationVM2
        get() = dirtyInjectionCache.get()

    val advancedCategorizeVM: AdvancedCategorizeVM
        get() = dirtyInjectionCache.get()

    val budgetedVM: BudgetedVM
        get() = dirtyInjectionCache.get()

    val categorizeVM: CategorizeVM
        get() = dirtyInjectionCache.get()

    val historyVM: HistoryVM
        get() = dirtyInjectionCache.get()

    val transactionsVM: TransactionsVM
        get() = dirtyInjectionCache.get()
}