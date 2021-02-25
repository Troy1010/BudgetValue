package com.tminus1010.budgetvalue.dependency_injection

import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.layer_domain.DatePeriodGetter
import com.tminus1010.budgetvalue.layer_domain.Domain
import com.tminus1010.budgetvalue.layer_ui.*
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules=[
    MiscModule::class,
    BudgetValueDBModule::class,
    RepoModule::class
])
interface AppComponent {
    fun getRepo(): Repo
    fun getDomain(): Domain
    fun getDatePeriodGetter(): DatePeriodGetter
    fun getAccountsVM(): AccountsVM
    fun getActivePlanVM(): ActivePlanVM
    fun getActiveReconciliationVM(): ActiveReconciliationVM
    fun getAdvancedCategorizeVM(): AdvancedCategorizeVM
    fun getBudgetedVM(): BudgetedVM
    fun getCategorizeVM(): CategorizeVM
    fun getHistoryVM(): HistoryVM
    fun getTransactionsVM(): TransactionsVM
    fun getActiveReconciliationVM2(): ActiveReconciliationVM2
}