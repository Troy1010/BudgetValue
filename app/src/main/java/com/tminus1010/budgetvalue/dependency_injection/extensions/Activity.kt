package com.tminus1010.budgetvalue.dependency_injection.extensions

import androidx.appcompat.app.AppCompatActivity
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.extensions.vm
import com.tminus1010.budgetvalue.layer_ui.*


val AppCompatActivity.app
    get() = application as App

val AppCompatActivity.appComponent
    get() = app.appComponent

val AppCompatActivity.repo
    get() = appComponent.getRepo()

val AppCompatActivity.domain
    get() = appComponent.getDomain()

val AppCompatActivity.accountsVM: AccountsVM
    get() = vm(appComponent)

val AppCompatActivity.activePlanVM: ActivePlanVM
    get() = vm(appComponent)

val AppCompatActivity.activeReconciliationVM: ActiveReconciliationVM
    get() = vm(appComponent)

val AppCompatActivity.activeReconciliationVM2: ActiveReconciliationVM2
    get() = vm(appComponent)

val AppCompatActivity.advancedCategorizeVM: AdvancedCategorizeVM
    get() = vm(appComponent)

val AppCompatActivity.budgetedVM: BudgetedVM
    get() = vm(appComponent)

val AppCompatActivity.categorizeVM: CategorizeVM
    get() = vm(appComponent)

val AppCompatActivity.historyVM: HistoryVM
    get() = vm(appComponent)

val AppCompatActivity.transactionsVM: TransactionsVM
    get() = vm(appComponent)