package com.tminus1010.budgetvalue.extensions_intersecting

import androidx.appcompat.app.AppCompatActivity
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.extensions.viewModels2


val AppCompatActivity.app
    get() = application as App

val AppCompatActivity.appComponent
    get() = app.appComponent

val AppCompatActivity.repo
    get() = appComponent.getRepo()

val AppCompatActivity.domain
    get() = appComponent.getDomain()

val AppCompatActivity.accountsVM
    get() = viewModels2 { appComponent.getAccountsVM() }.value

val AppCompatActivity.activePlanVM
    get() = viewModels2 { appComponent.getActivePlanVM() }.value

val AppCompatActivity.activeReconciliationVM
    get() = viewModels2 { appComponent.getActiveReconciliationVM() }.value

val AppCompatActivity.advancedCategorizeVM
    get() = viewModels2 { appComponent.getAdvancedCategorizeVM() }.value

val AppCompatActivity.budgetedVM
    get() = viewModels2 { appComponent.getBudgetedVM() }.value

val AppCompatActivity.categorizeVM
    get() = viewModels2 { appComponent.getCategorizeVM() }.value

val AppCompatActivity.historyVM
    get() = viewModels2 { appComponent.getHistoryVM() }.value

val AppCompatActivity.transactionsVM
    get() = viewModels2 { appComponent.getTransactionsVM() }.value