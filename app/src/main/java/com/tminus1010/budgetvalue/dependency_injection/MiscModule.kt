package com.tminus1010.budgetvalue.dependency_injection

import com.tminus1010.budgetvalue.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MiscModule(val app: App) {
    @Provides
    @Singleton
    fun providesApp(): App = app
}