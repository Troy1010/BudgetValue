package com.tminus1010.buva.environment

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.tminus1010.buva.environment.adapter.RoomWithCategoriesTypeConverter
import com.tminus1010.buva.environment.android_wrapper.AndroidNavigationWrapper
import com.tminus1010.buva.environment.android_wrapper.AndroidNavigationWrapperImpl
import com.tminus1010.buva.environment.room.CategoryDatabase
import com.tminus1010.buva.environment.room.Migrations
import com.tminus1010.buva.environment.room.MiscDAO
import com.tminus1010.buva.environment.room.MiscDatabase
import com.tminus1010.buva.environment.room.UserCategoriesDAO
import com.tminus1010.tmcommonkotlin.imagetotext.ImageToText
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
open class EnvironmentModule {
    @Provides
    @Reusable
    open fun provideDataStore(application: Application): DataStore<Preferences> =
        application.dataStore

    @Provides
    @Reusable
    open fun providesCategoryDatabase(application: Application): CategoryDatabase =
        Room.databaseBuilder(application, CategoryDatabase::class.java, "CategoryDatabase")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Reusable
    open fun providesMiscDatabase(application: Application, roomWithCategoriesTypeConverter: RoomWithCategoriesTypeConverter, migrations: Migrations): MiscDatabase =
        Room.databaseBuilder(application, MiscDatabase::class.java, "MiscDatabase")
            .addMigrations(migrations.z43_44)
            .addTypeConverter(roomWithCategoriesTypeConverter)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Reusable
    open fun providesSharedPreferences(application: Application): SharedPreferences =
        application.getSharedPreferences("SharedPref", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun providesMiscDao(roomDatabase: MiscDatabase): MiscDAO = roomDatabase.miscDAO()

    @Provides
    @Singleton
    fun provideUserCategoriesDAO(categoryDatabase: CategoryDatabase): UserCategoriesDAO = categoryDatabase.userCategoriesDAO()

    @Provides
    fun provideImageToText(application: Application): ImageToText = ImageToText(application)

    @Provides
    fun provideAndroidNavigationWrapper(): AndroidNavigationWrapper = AndroidNavigationWrapperImpl()
}