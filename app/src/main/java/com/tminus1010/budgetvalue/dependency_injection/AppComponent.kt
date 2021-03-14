package com.tminus1010.budgetvalue.dependency_injection

import com.tminus1010.budgetvalue.IFlavorIntersection
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.layer_domain.Domain
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
}