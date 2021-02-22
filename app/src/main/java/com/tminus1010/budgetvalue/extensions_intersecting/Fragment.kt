package com.tminus1010.budgetvalue.extensions_intersecting

import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.App


val Fragment.app
    get() = requireActivity().application as App

val Fragment.appComponent
    get() = app.appComponent

val Fragment.repo
    get() = appComponent.getRepo()

val Fragment.domain
    get() = appComponent.getDomain()

val Fragment.accountsVM
    get() = appComponent.getAccountsVM()

val Fragment.activePlanVM
    get() = appComponent.getActivePlanVM()

val Fragment.activeReconciliationVM
    get() = appComponent.getActiveReconciliationVM()

val Fragment.advancedCategorizeVM
    get() = appComponent.getAdvancedCategorizeVM()

val Fragment.budgetedVM
    get() = appComponent.getBudgetedVM()

val Fragment.categorizeVM
    get() = appComponent.getCategorizeVM()

val Fragment.historyVM
    get() = appComponent.getHistoryVM()

val Fragment.transactionsVM
    get() = appComponent.getTransactionsVM()