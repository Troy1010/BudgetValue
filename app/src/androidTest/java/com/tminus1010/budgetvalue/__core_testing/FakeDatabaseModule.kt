package com.tminus1010.budgetvalue.__core_testing

import android.app.Application
import androidx.room.Room
import com.tminus1010.budgetvalue._core.all.dependency_injection.DatabaseModule
import com.tminus1010.budgetvalue._core.data.MiscDatabase
import com.tminus1010.budgetvalue._core.data.RoomWithCategoriesTypeConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class],
)
object FakeDatabaseModule {
    @Provides
    @Singleton
    fun providesMiscDatabase(
        application: Application,
        roomWithCategoriesTypeConverter: RoomWithCategoriesTypeConverter,
    ): MiscDatabase {
        return Room.inMemoryDatabaseBuilder(application, MiscDatabase::class.java)
            .addTypeConverter(roomWithCategoriesTypeConverter)
            .fallbackToDestructiveMigration()
            .build()
    }
}