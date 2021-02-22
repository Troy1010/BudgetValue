package com.tminus1010.budgetvalue.extensions_intersecting

import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.extensions.activityViewModels2


val Fragment.app
    get() = requireActivity().application as App

val Fragment.appComponent
    get() = app.appComponent

val Fragment.repo
    get() = appComponent.getRepo()

val Fragment.domain
    get() = appComponent.getDomain()

val Fragment.accountsVM
    get() = activityViewModels2 { appComponent.getAccountsVM() }.value

val Fragment.activePlanVM
    get() = activityViewModels2 { appComponent.getActivePlanVM() }.value

val Fragment.activeReconciliationVM
    get() = activityViewModels2 { appComponent.getActiveReconciliationVM() }.value

val Fragment.advancedCategorizeVM
    get() = activityViewModels2 { appComponent.getAdvancedCategorizeVM() }.value

val Fragment.budgetedVM
    get() = activityViewModels2 { appComponent.getBudgetedVM() }.value

val Fragment.categorizeVM
    get() = activityViewModels2 { appComponent.getCategorizeVM() }.value

val Fragment.historyVM
    get() = activityViewModels2 { appComponent.getHistoryVM() }.value

val Fragment.transactionsVM
    get() = activityViewModels2 { appComponent.getTransactionsVM() }.value