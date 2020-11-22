package com.example.budgetvalue.dependency_injection

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules= [MiscModule::class, BudgetValueDBModuleMock::class])
interface AppComponentMock : AppComponent