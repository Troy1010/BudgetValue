package com.tminus1010.budgetvalue.dependency_injection

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tminus1010.budgetvalue.*
import com.tminus1010.budgetvalue.layer_data.MoshiAdapters
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
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(MoshiAdapters)
            .addLast(KotlinJsonAdapterFactory())
            .build()
}