package com.tminus1010.budgetvalue.layer_ui

import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.dependency_injection.ViewModelProviders
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
interface IViewModelFrag {
    val viewModelProviders: ViewModelProviders
    val Fragment.accountsVM get() = viewModelProviders.accountsVM
    val Fragment.activePlanVM get() = viewModelProviders.activePlanVM
    val Fragment.activeReconciliationVM get() = viewModelProviders.activeReconciliationVM
    val Fragment.activeReconciliationVM2 get() = viewModelProviders.activeReconciliationVM2
    val Fragment.categorizeAdvancedVM get() = viewModelProviders.categorizeAdvancedVM
    val Fragment.categorizeVM get() = viewModelProviders.categorizeVM
    val Fragment.historyVM get() = viewModelProviders.historyVM
    val Fragment.transactionsVM get() = viewModelProviders.transactionsVM
    val Fragment.errorVM get() = viewModelProviders.errorVM
    val Fragment.budgetedVM get() = viewModelProviders.budgetedVM
}