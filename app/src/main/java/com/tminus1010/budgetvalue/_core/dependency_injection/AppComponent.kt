package com.tminus1010.budgetvalue._core.dependency_injection

import com.tminus1010.budgetvalue.plans.PlanUseCases
import com.tminus1010.budgetvalue._core.shared_features.date_period_getter.DatePeriodGetter
import com.tminus1010.budgetvalue._core.data.RepoFacade
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.budgetvalue._layer_facades.FlavorFacade
import dagger.Component
import dagger.hilt.DefineComponent
import javax.inject.Singleton

@Singleton
@Component(modules = [
    MiscModule::class,
    RepoRoomDBModule::class,
    RepoModule::class,
    BindingModule::class,
    FlavorContractModule::class
])
@DefineComponent
interface AppComponent {
    fun getRepo(): RepoFacade
    fun getDomain(): DomainFacade
    fun getDatePeriodGetter(): DatePeriodGetter
    fun getPlanUseCases(): PlanUseCases
    fun getFlavorFacade(): FlavorFacade
}