package com.tminus1010.budgetvalue._core.dependency_injection

import android.app.Application
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tminus1010.budgetvalue._core.App
import com.tminus1010.budgetvalue._core.data.MoshiAdapters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MiscModule {
    @Provides
    @Singleton
    fun providesApp(application: Application): App = application as App

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(MoshiAdapters)
            .addLast(KotlinJsonAdapterFactory())
            .build()
}