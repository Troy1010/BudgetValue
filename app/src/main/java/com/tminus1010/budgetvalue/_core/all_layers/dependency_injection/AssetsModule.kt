package com.tminus1010.budgetvalue._core.all_layers.dependency_injection

import android.app.Application
import android.content.res.AssetManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AssetsModule {
    @Provides
    @Singleton
    fun provideAssets(application: Application): AssetManager = application.assets
}