package com.example.budgetvalue.dependency_injection

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules= [AppModule::class, MockDBModule::class])
interface MockAppComponent : AppComponent