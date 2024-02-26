package com.tminus1010.buva.core_testing

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.tminus1010.buva.core_testing.shared.FakeDataStore
import com.tminus1010.buva.environment.EnvironmentModule
import com.tminus1010.buva.environment.adapter.RoomWithCategoriesTypeConverter
import com.tminus1010.buva.environment.room.CategoryDatabase
import com.tminus1010.buva.environment.room.Migrations
import com.tminus1010.buva.environment.room.MiscDatabase


class BaseFakeEnvironmentModule : EnvironmentModule() {
    override fun provideDataStore(application: Application): DataStore<Preferences> {
        return FakeDataStore()
    }

    override fun providesCategoryDatabase(application: Application): CategoryDatabase {
        return Room.inMemoryDatabaseBuilder(app, CategoryDatabase::class.java).build()
    }

    override fun providesMiscDatabase(application: Application, roomWithCategoriesTypeConverter: RoomWithCategoriesTypeConverter, migrations: Migrations): MiscDatabase {
        return Room.inMemoryDatabaseBuilder(app, MiscDatabase::class.java)
            .addTypeConverter(roomWithCategoriesTypeConverter)
            .fallbackToDestructiveMigration()
            .build()
    }
}