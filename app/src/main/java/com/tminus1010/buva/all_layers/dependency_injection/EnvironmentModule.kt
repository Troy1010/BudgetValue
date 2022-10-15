package com.tminus1010.buva.all_layers.dependency_injection

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.tminus1010.buva.data.service.*
import com.tminus1010.tmcommonkotlin.imagetotext.ImageToText
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object EnvironmentModule {
    @Provides
    @Reusable
    fun provideDataStore(application: Application): DataStore<Preferences> =
        application.dataStore

    @Provides
    @Reusable
    fun providesCategoryDatabase(application: Application): CategoryDatabase =
        Room.databaseBuilder(application, CategoryDatabase::class.java, "CategoryDatabase")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Reusable
    fun providesMiscDatabase(application: Application, roomWithCategoriesTypeConverter: RoomWithCategoriesTypeConverter, migrations: Migrations): MiscDatabase =
        Room.databaseBuilder(application, MiscDatabase::class.java, "MiscDatabase")
            .addMigrations(migrations.z43_44)
            .addTypeConverter(roomWithCategoriesTypeConverter)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Reusable
    fun providesSharedPreferences(application: Application): SharedPreferences =
        application.getSharedPreferences("SharedPref", Context.MODE_PRIVATE)

    @Provides
    fun provideImageToText(application: Application): ImageToText = ImageToText(application)
}