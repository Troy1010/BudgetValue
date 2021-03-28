package com.tminus1010.budgetvalue._core.dependency_injection

//class ViewModelProviders(val activity: FragmentActivity, val appComponent: AppComponent) {
//    private val c get() = appComponent
//    val reconciliationsVM: ReconciliationsVM
//            by { ReconciliationsVM(c.getDomain()) }
//                .let { activity.viewModels { createViewModelFactory(it) } }
//    val plansVM: PlansVM
//            by { PlansVM(c.getDomain()) }
//                .let { activity.viewModels { createViewModelFactory(it) } }
//    val categoriesVM2: CategoriesVM2
//            by { CategoriesVM2(c.getDomain(), c.getPlanUseCases(), activePlanVM) }
//                .let { activity.viewModels { createViewModelFactory(it) } }
//    val categoriesVM: CategoriesVM
//            by { CategoriesVM(c.getDomain()) }
//                .let { activity.viewModels { createViewModelFactory(it) } }
//    val accountsVM: AccountsVM
//            by { AccountsVM(c.getDomain()) }
//                .let { activity.viewModels { createViewModelFactory(it) } }
//    val activePlanVM: ActivePlanVM
//            by { ActivePlanVM(c.getDomain(), categoriesVM, c.getDatePeriodGetter(), plansVM) }
//                .let { activity.viewModels { createViewModelFactory(it) } }
//    val activeReconciliationVM: ActiveReconciliationVM
//            by { ActiveReconciliationVM(c.getDomain(), categoriesVM) }
//                .let { activity.viewModels { createViewModelFactory(it) } }
//    val activeReconciliationVM2: ActiveReconciliationVM2
//            by { ActiveReconciliationVM2(activeReconciliationVM, budgetedVM, c.getDomain(), transactionsVM) }
//                .let { activity.viewModels { createViewModelFactory(it) } }
//    val categorizeTransactionsAdvancedVM: CategorizeTransactionsAdvancedVM
//            by { CategorizeTransactionsAdvancedVM(c.getDomain(), categorizeTransactionsVM) }
//                .let { activity.viewModels { createViewModelFactory(it) } }
//    val budgetedVM: BudgetedVM
//            by { BudgetedVM(c.getDomain(), transactionsVM, activeReconciliationVM, accountsVM) }
//                .let { activity.viewModels { createViewModelFactory(it) } }
//    val categorizeTransactionsVM: CategorizeTransactionsVM
//            by { CategorizeTransactionsVM(c.getDomain(), transactionsVM) }
//                .let { activity.viewModels { createViewModelFactory(it) } }
//    val historyVM: HistoryVM
//            by { HistoryVM(c.getDomain(), transactionsVM, activeReconciliationVM, activeReconciliationVM2, budgetedVM) }
//                .let { activity.viewModels { createViewModelFactory(it) } }
//    val transactionsVM: TransactionsVM
//            by { TransactionsVM(c.getDomain()) }
//                .let { activity.viewModels { createViewModelFactory(it) } }
//    val errorVM: ErrorVM
//            by { ErrorVM() }
//                .let { activity.viewModels { createViewModelFactory(it) } }
//}