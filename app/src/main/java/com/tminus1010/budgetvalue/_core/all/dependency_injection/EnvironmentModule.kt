package com.tminus1010.budgetvalue._core.all.dependency_injection

import android.app.Application
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.tminus1010.budgetvalue._core.data.CategoryDatabase
import com.tminus1010.budgetvalue._core.data.MiscDatabase
import com.tminus1010.budgetvalue._core.data.RoomWithCategoriesTypeConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EnvironmentModule : IEnvironmentModule {
    @Provides
    @Singleton
    override fun provideDataStore(application: Application): DataStore<Preferences> =
        super.provideDataStore(application)

    @Provides
    @Singleton
    override fun providesCategoryDatabase(application: Application): CategoryDatabase =
        super.providesCategoryDatabase(application)

    @Provides
    @Singleton
    override fun providesMiscDatabase(application: Application, roomWithCategoriesTypeConverter: RoomWithCategoriesTypeConverter): MiscDatabase =
        super.providesMiscDatabase(application, roomWithCategoriesTypeConverter)

    @Provides
    @Singleton
    override fun providesSharedPreferences(application: Application): SharedPreferences =
        super.providesSharedPreferences(application)
}