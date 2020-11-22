package com.example.budgetvalue.dependency_injection

import com.example.budgetvalue.layer_data.Repo
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
}