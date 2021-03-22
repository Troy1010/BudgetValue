package com.tminus1010.budgetvalue.features_shared

import androidx.lifecycle.LifecycleOwner
import com.tminus1010.budgetvalue.dependency_injection.ViewModelProviders

interface IViewModels {
    val viewModelProviders: ViewModelProviders
    val LifecycleOwner.accountsVM get() = viewModelProviders.accountsVM
    val LifecycleOwner.activePlanVM get() = viewModelProviders.activePlanVM
    val LifecycleOwner.activeReconciliationVM get() = viewModelProviders.activeReconciliationVM
    val LifecycleOwner.activeReconciliationVM2 get() = viewModelProviders.activeReconciliationVM2
    val LifecycleOwner.categorizeAdvancedVM get() = viewModelProviders.categorizeAdvancedVM
    val LifecycleOwner.categorizeVM get() = viewModelProviders.categorizeTransactionsVM
    val LifecycleOwner.historyVM get() = viewModelProviders.historyVM
    val LifecycleOwner.transactionsVM get() = viewModelProviders.transactionsVM
    val LifecycleOwner.errorVM get() = viewModelProviders.errorVM
    val LifecycleOwner.budgetedVM get() = viewModelProviders.budgetedVM
}