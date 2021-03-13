package com.tminus1010.budgetvalue.dependency_injection

import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.FlavorIntersection
import com.tminus1010.budgetvalue.IFlavorIntersection
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MiscModule(val app: App) {
    @Provides
    @Singleton
    fun providesApp(): App = app

    @Provides
    @Singleton
    fun provideFlavorIntersection(flavorIntersection: FlavorIntersection): IFlavorIntersection = flavorIntersection
}