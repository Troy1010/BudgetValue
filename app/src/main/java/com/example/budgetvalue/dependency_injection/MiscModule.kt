package com.example.budgetvalue.dependency_injection

import com.example.budgetvalue.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MiscModule(val app: App) {
    @Provides
    @Singleton
    fun providesApp(): App = app
}