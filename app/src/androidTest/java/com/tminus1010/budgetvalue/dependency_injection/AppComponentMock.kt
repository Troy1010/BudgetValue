package com.tminus1010.budgetvalue.dependency_injection

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules=[
    MiscModule::class,
    BudgetValueDBModuleMock::class,
    RepoModule::class
])
interface AppComponentMock : AppComponent