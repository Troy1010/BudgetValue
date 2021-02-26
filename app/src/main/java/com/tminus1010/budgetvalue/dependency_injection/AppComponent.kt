package com.tminus1010.budgetvalue.dependency_injection

import com.tminus1010.budgetvalue.dependency_injection.view_model_provision.IVMComponent
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.layer_domain.DatePeriodGetter
import com.tminus1010.budgetvalue.layer_domain.Domain
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    MiscModule::class,
    BudgetValueDBModule::class,
    RepoModule::class
])
interface AppComponent : IVMComponent {
    fun getRepo(): Repo
    fun getDomain(): Domain
    fun getDatePeriodGetter(): DatePeriodGetter
}