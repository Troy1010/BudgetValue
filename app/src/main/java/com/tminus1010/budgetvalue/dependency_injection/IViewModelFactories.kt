package com.tminus1010.budgetvalue.dependency_injection

import com.tminus1010.budgetvalue.layer_ui.*

interface IViewModelFactories {
    val viewModelFactoriesHelper: ViewModelFactoriesHelper
    private val c get() = viewModelFactoriesHelper.component

    val accountsVM: AccountsVM
        get() = viewModelFactoriesHelper.get { AccountsVM(c.getRepo()) }

    val activePlanVM: ActivePlanVM
        get() = viewModelFactoriesHelper.get { ActivePlanVM(c.getRepo(), c.getDomain()) }

    val activeReconciliationVM: ActiveReconciliationVM
        get() = viewModelFactoriesHelper.get { ActiveReconciliationVM(c.getRepo(), transactionsVM, accountsVM, activePlanVM) }

    val activeReconciliationVM2: ActiveReconciliationVM2
        get() = viewModelFactoriesHelper.get { ActiveReconciliationVM2(activeReconciliationVM, budgetedVM) }

    val advancedCategorizeVM: AdvancedCategorizeVM
        get() = viewModelFactoriesHelper.get { AdvancedCategorizeVM(categorizeVM) }

    val budgetedVM: BudgetedVM
        get() = viewModelFactoriesHelper.get { BudgetedVM(c.getRepo(), transactionsVM, activeReconciliationVM, accountsVM) }

    val categorizeVM: CategorizeVM
        get() = viewModelFactoriesHelper.get { CategorizeVM(c.getRepo(), transactionsVM) }

    val historyVM: HistoryVM
        get() = viewModelFactoriesHelper.get { HistoryVM(c.getRepo(), transactionsVM, activeReconciliationVM, activeReconciliationVM2, c.getDomain(), budgetedVM) }

    val transactionsVM: TransactionsVM
        get() = viewModelFactoriesHelper.get { TransactionsVM(c.getRepo(), c.getDomain()) }
}