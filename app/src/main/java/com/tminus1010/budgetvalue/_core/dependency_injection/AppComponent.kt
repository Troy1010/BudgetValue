package com.tminus1010.budgetvalue._core.dependency_injection

import com.tminus1010.budgetvalue._core.IFlavorIntersection
import com.tminus1010.budgetvalue.plans.PlanUseCases
import com.tminus1010.budgetvalue._core.shared_features.date_period_getter.DatePeriodGetter
import com.tminus1010.budgetvalue._core.data.Repo
import com.tminus1010.budgetvalue._layer_facades.Domain
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    MiscModule::class,
    RepoRoomDBModule::class,
    RepoModule::class,
    BindingModule::class
])
interface AppComponent {
    fun getRepo(): Repo
    fun getDomain(): Domain
    fun getFlavorIntersection(): IFlavorIntersection
    fun getDatePeriodGetter(): DatePeriodGetter
    fun getPlanUseCases(): PlanUseCases
}