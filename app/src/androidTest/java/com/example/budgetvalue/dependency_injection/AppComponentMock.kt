package com.example.budgetvalue.dependency_injection

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules= [AppModule::class, DBModuleMock::class])
interface AppComponentMock : AppComponent