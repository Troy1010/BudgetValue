package com.tminus1010.budgetvalue.dependency_injection

import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.tminus1010.budgetvalue.features.accounts.AccountsVM
import com.tminus1010.budgetvalue.features.categories.CategoriesVM
import com.tminus1010.budgetvalue.features.categories.CategoriesVM2
import com.tminus1010.budgetvalue.features.plans.ActivePlanVM
import com.tminus1010.budgetvalue.features_shared.budgeted.BudgetedVM
import com.tminus1010.budgetvalue.features_shared.history.HistoryVM
import com.tminus1010.budgetvalue.features.reconciliations.ActiveReconciliationVM
import com.tminus1010.budgetvalue.features.reconciliations.ActiveReconciliationVM2
import com.tminus1010.budgetvalue.features.transactions.CategorizeTransactionsAdvancedVM
import com.tminus1010.budgetvalue.features.transactions.CategorizeTransactionsVM
import com.tminus1010.budgetvalue.features.transactions.TransactionsVM
import com.tminus1010.budgetvalue.features_shared.ErrorVM
import com.tminus1010.tmcommonkotlin.view.createViewModelFactory

class ViewModelProviders(val activity: FragmentActivity, val appComponent: AppComponent) {
    private val c get() = appComponent
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
            by { ActivePlanVM(c.getDomain(), categoriesVM, c.getDatePeriodGetter()) }
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
            by { BudgetedVM(c.getDomain(), transactionsVM, activeReconciliationVM, accountsVM, categoriesVM) }
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