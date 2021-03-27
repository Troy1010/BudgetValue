package com.tminus1010.budgetvalue._layer_facades

import androidx.lifecycle.LifecycleOwner
import com.tminus1010.budgetvalue._core.dependency_injection.ViewModelProviders

interface IViewModels {
    val viewModelProviders: ViewModelProviders
    val LifecycleOwner.reconciliationsVM get() = viewModelProviders.reconciliationsVM
    val LifecycleOwner.plansVM get() = viewModelProviders.plansVM
    val LifecycleOwner.categoryDeletionVM get() = viewModelProviders.categoriesVM2
    val LifecycleOwner.categoriesVM get() = viewModelProviders.categoriesVM
    val LifecycleOwner.accountsVM get() = viewModelProviders.accountsVM
    val LifecycleOwner.activePlanVM get() = viewModelProviders.activePlanVM
    val LifecycleOwner.activeReconciliationVM get() = viewModelProviders.activeReconciliationVM
    val LifecycleOwner.activeReconciliationVM2 get() = viewModelProviders.activeReconciliationVM2
    val LifecycleOwner.categorizeAdvancedVM get() = viewModelProviders.categorizeTransactionsAdvancedVM
    val LifecycleOwner.categorizeVM get() = viewModelProviders.categorizeTransactionsVM
    val LifecycleOwner.historyVM get() = viewModelProviders.historyVM
    val LifecycleOwner.transactionsVM get() = viewModelProviders.transactionsVM
    val LifecycleOwner.errorVM get() = viewModelProviders.errorVM
    val LifecycleOwner.budgetedVM get() = viewModelProviders.budgetedVM
}