package com.tminus1010.budgetvalue.dependency_injection

import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.tminus1010.budgetvalue.modules.accounts.AccountsVM
import com.tminus1010.budgetvalue.modules.plans.ActivePlanVM
import com.tminus1010.budgetvalue.modules_shared.BudgetedVM
import com.tminus1010.budgetvalue.modules_shared.HistoryVM
import com.tminus1010.budgetvalue.modules.reconciliations.ActiveReconciliationVM
import com.tminus1010.budgetvalue.modules.reconciliations.ActiveReconciliationVM2
import com.tminus1010.budgetvalue.modules.transactions.CategorizeAdvancedVM
import com.tminus1010.budgetvalue.modules.transactions.CategorizeTransactionsVM
import com.tminus1010.budgetvalue.modules.transactions.TransactionsVM
import com.tminus1010.budgetvalue.modules_shared.ErrorVM
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
            by { ActiveReconciliationVM(c.getDomain(), transactionsVM, activePlanVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val activeReconciliationVM2: ActiveReconciliationVM2
            by { ActiveReconciliationVM2(activeReconciliationVM, budgetedVM, c.getDomain(), transactionsVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val categorizeAdvancedVM: CategorizeAdvancedVM
            by { CategorizeAdvancedVM(c.getDomain(), categorizeTransactionsVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val budgetedVM: BudgetedVM
            by { BudgetedVM(c.getDomain(), transactionsVM, activeReconciliationVM, accountsVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val categorizeTransactionsVM: CategorizeTransactionsVM
            by { CategorizeTransactionsVM(c.getDomain(), transactionsVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val historyVM: HistoryVM
            by { HistoryVM(c.getDomain(), transactionsVM, activeReconciliationVM, activeReconciliationVM2, budgetedVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val transactionsVM: TransactionsVM
            by { TransactionsVM(c.getDomain()) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val errorVM: ErrorVM
            by { ErrorVM() }
                .let { activity.viewModels { createViewModelFactory(it) } }
}