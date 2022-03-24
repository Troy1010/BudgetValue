package com.tminus1010.budgetvalue.all_layers.dependency_injection

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.tminus1010.budgetvalue.data.service.*

interface IEnvironmentModule {
    fun provideDataStore(application: Application): DataStore<Preferences> =
        application.dataStore

    fun providesCategoryDatabase(application: Application): CategoryDatabase =
        Room.databaseBuilder(application, CategoryDatabase::class.java, "CategoryDatabase")
            .fallbackToDestructiveMigration()
            .build()

    fun providesMiscDatabase(application: Application, roomWithCategoriesTypeConverter: RoomWithCategoriesTypeConverter): MiscDatabase =
        Room.databaseBuilder(application, MiscDatabase::class.java, "MiscDatabase")
            .addMigrations(Migrations.z43_44(MoshiProvider.moshi))
            .addTypeConverter(roomWithCategoriesTypeConverter)
            .fallbackToDestructiveMigration()
            .build()

    fun providesSharedPreferences(application: Application): SharedPreferences =
        application.getSharedPreferences(
            "SharedPref",
            Context.MODE_PRIVATE
        )
}