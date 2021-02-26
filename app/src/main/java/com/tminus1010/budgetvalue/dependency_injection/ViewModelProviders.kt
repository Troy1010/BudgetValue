package com.tminus1010.budgetvalue.dependency_injection

import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.tminus1010.budgetvalue.layer_ui.*
import com.tminus1010.tmcommonkotlin.view.createViewModelFactory

class ViewModelProviders(val activity: FragmentActivity, val c: AppComponent) {
    val accountsVM: AccountsVM
            by { AccountsVM(c.getRepo()) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val activePlanVM: ActivePlanVM
            by { ActivePlanVM(c.getRepo(), c.getDomain()) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val activeReconciliationVM: ActiveReconciliationVM
            by { ActiveReconciliationVM(c.getRepo(), transactionsVM, accountsVM, activePlanVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val activeReconciliationVM2: ActiveReconciliationVM2
            by { ActiveReconciliationVM2(activeReconciliationVM, budgetedVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val advancedCategorizeVM: AdvancedCategorizeVM
            by { AdvancedCategorizeVM(categorizeVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val budgetedVM: BudgetedVM
            by { BudgetedVM(c.getRepo(), transactionsVM, activeReconciliationVM, accountsVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val categorizeVM: CategorizeVM
            by { CategorizeVM(c.getRepo(), transactionsVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val historyVM: HistoryVM
            by { HistoryVM(c.getRepo(), transactionsVM, activeReconciliationVM, activeReconciliationVM2, c.getDomain(), budgetedVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val transactionsVM: TransactionsVM
            by { TransactionsVM(c.getRepo(), c.getDomain()) }
                .let { activity.viewModels { createViewModelFactory(it) } }
}