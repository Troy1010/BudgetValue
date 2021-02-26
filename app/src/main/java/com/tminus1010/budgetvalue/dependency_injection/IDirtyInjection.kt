package com.tminus1010.budgetvalue.dependency_injection

import com.tminus1010.budgetvalue.layer_ui.*

interface IDirtyInjection {
    val dirtyInjectionCache: DirtyInjectionCache
    private val c get() = dirtyInjectionCache.component

    val accountsVM: AccountsVM
        get() = dirtyInjectionCache.get { AccountsVM(c.getRepo()) }

    val activePlanVM: ActivePlanVM
        get() = dirtyInjectionCache.get { ActivePlanVM(c.getRepo(), c.getDomain()) }

    val activeReconciliationVM: ActiveReconciliationVM
        get() = dirtyInjectionCache.get { ActiveReconciliationVM(c.getRepo(), transactionsVM, accountsVM, activePlanVM) }

    val activeReconciliationVM2: ActiveReconciliationVM2
        get() = dirtyInjectionCache.get { ActiveReconciliationVM2(activeReconciliationVM, budgetedVM) }

    val advancedCategorizeVM: AdvancedCategorizeVM
        get() = dirtyInjectionCache.get { AdvancedCategorizeVM(categorizeVM) }

    val budgetedVM: BudgetedVM
        get() = dirtyInjectionCache.get { BudgetedVM(c.getRepo(), transactionsVM, activeReconciliationVM, accountsVM) }

    val categorizeVM: CategorizeVM
        get() = dirtyInjectionCache.get { CategorizeVM(c.getRepo(), transactionsVM) }

    val historyVM: HistoryVM
        get() = dirtyInjectionCache.get { HistoryVM(c.getRepo(), transactionsVM, activeReconciliationVM, activeReconciliationVM2, c.getDomain(), budgetedVM) }

    val transactionsVM: TransactionsVM
        get() = dirtyInjectionCache.get { TransactionsVM(c.getRepo(), c.getDomain()) }
}