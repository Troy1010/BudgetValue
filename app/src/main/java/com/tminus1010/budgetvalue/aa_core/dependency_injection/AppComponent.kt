package com.tminus1010.budgetvalue.aa_core.dependency_injection

import com.tminus1010.budgetvalue.aa_core.IFlavorIntersection
import com.tminus1010.budgetvalue.plans.PlanUseCases
import com.tminus1010.budgetvalue.aa_shared.domain.DatePeriodGetter
import com.tminus1010.budgetvalue.aa_core.data.Repo
import com.tminus1010.budgetvalue.aa_shared.domain.Domain
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