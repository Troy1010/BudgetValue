package com.tminus1010.budgetvalue.dependency_injection.injection_extensions

import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.extensions.avm
import com.tminus1010.budgetvalue.layer_ui.*


val Fragment.app
    get() = requireActivity().application as App

val Fragment.appComponent
    get() = app.appComponent

val Fragment.repo
    get() = appComponent.getRepo()

val Fragment.domain
    get() = appComponent.getDomain()

val Fragment.accountsVM: AccountsVM
    get() = avm(appComponent)

val Fragment.activePlanVM: ActivePlanVM
    get() = avm(appComponent)

val Fragment.activeReconciliationVM: ActiveReconciliationVM
    get() = avm(appComponent)

val Fragment.activeReconciliationVM2: ActiveReconciliationVM2
    get() = avm(appComponent)

val Fragment.advancedCategorizeVM: AdvancedCategorizeVM
    get() = avm(appComponent)

val Fragment.budgetedVM: BudgetedVM
    get() = avm(appComponent)

val Fragment.categorizeVM: CategorizeVM
    get() = avm(appComponent)

val Fragment.historyVM: HistoryVM
    get() = avm(appComponent)

val Fragment.transactionsVM: TransactionsVM
    get() = avm(appComponent)