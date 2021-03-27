package com.tminus1010.budgetvalue.aa_core.dependency_injection

import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.tminus1010.budgetvalue.accounts.AccountsVM
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue.categories.CategoriesVM2
import com.tminus1010.budgetvalue.plans.ActivePlanVM
import com.tminus1010.budgetvalue.budgeted.BudgetedVM
import com.tminus1010.budgetvalue.history.HistoryVM
import com.tminus1010.budgetvalue.plans.PlansVM
import com.tminus1010.budgetvalue.reconciliations.ActiveReconciliationVM
import com.tminus1010.budgetvalue.reconciliations.ActiveReconciliationVM2
import com.tminus1010.budgetvalue.reconciliations.ReconciliationsVM
import com.tminus1010.budgetvalue.transactions.CategorizeTransactionsAdvancedVM
import com.tminus1010.budgetvalue.transactions.CategorizeTransactionsVM
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import com.tminus1010.budgetvalue.aa_core.ErrorVM
import com.tminus1010.tmcommonkotlin.view.createViewModelFactory

class ViewModelProviders(val activity: FragmentActivity, val appComponent: AppComponent) {
    private val c get() = appComponent
    val reconciliationsVM: ReconciliationsVM
            by { ReconciliationsVM(c.getDomain()) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val plansVM: PlansVM
            by { PlansVM(c.getDomain()) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val categoriesVM2: CategoriesVM2
            by { CategoriesVM2(c.getDomain(), c.getPlanUseCases(), activePlanVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val categoriesVM: CategoriesVM
            by { CategoriesVM(c.getDomain()) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val accountsVM: AccountsVM
            by { AccountsVM(c.getDomain()) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val activePlanVM: ActivePlanVM
            by { ActivePlanVM(c.getDomain(), categoriesVM, c.getDatePeriodGetter(), plansVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val activeReconciliationVM: ActiveReconciliationVM
            by { ActiveReconciliationVM(c.getDomain(), categoriesVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val activeReconciliationVM2: ActiveReconciliationVM2
            by { ActiveReconciliationVM2(activeReconciliationVM, budgetedVM, c.getDomain(), transactionsVM) }
                .let { activity.viewModels { createViewModelFactory(it) } }
    val categorizeTransactionsAdvancedVM: CategorizeTransactionsAdvancedVM
            by { CategorizeTransactionsAdvancedVM(c.getDomain(), categorizeTransactionsVM) }
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