package com.tminus1010.budgetvalue.dependency_injection

import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.tminus1010.budgetvalue.layer_ui.*
import com.tminus1010.tmcommonkotlin.view.createViewModelFactory

class ViewModelProviders(val activity: FragmentActivity, val appComponent: AppComponent) {
    val c get() = appComponent
    val accountsVM: AccountsVM
            by { AccountsVM(c.getDomain()) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val activePlanVM: ActivePlanVM
            by { ActivePlanVM(c.getDomain()) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val activeReconciliationVM: ActiveReconciliationVM
            by { ActiveReconciliationVM(c.getDomain(), transactionsVM, accountsVM, activePlanVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val activeReconciliationVM2: ActiveReconciliationVM2
            by { ActiveReconciliationVM2(activeReconciliationVM, budgetedVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val categorizeAdvancedVM: CategorizeAdvancedVM
            by { CategorizeAdvancedVM(c.getDomain(), categorizeVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val budgetedVM: BudgetedVM
            by { BudgetedVM(c.getDomain(), transactionsVM, activeReconciliationVM, accountsVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val categorizeVM: CategorizeVM
            by { CategorizeVM(c.getDomain(), transactionsVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val historyVM: HistoryVM
            by { HistoryVM(c.getDomain(), transactionsVM, activeReconciliationVM, activeReconciliationVM2, budgetedVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val transactionsVM: TransactionsVM
            by { TransactionsVM(c.getDomain()) }
                .let { activity.viewModels { createViewModelFactory(it) } }
}