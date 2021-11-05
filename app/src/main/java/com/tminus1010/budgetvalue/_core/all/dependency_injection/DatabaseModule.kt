package com.tminus1010.budgetvalue._core.all.dependency_injection

import android.app.Application
import androidx.room.Room
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
object DatabaseModule {
    @Provides
    @Singleton
    fun providesCategoryDatabase(
        application: Application,
    ): CategoryDatabase {
        return Room.databaseBuilder(application, CategoryDatabase::class.java, "CategoryDatabase")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providesMiscDatabase(
        application: Application,
        roomWithCategoriesTypeConverter: RoomWithCategoriesTypeConverter,
    ): MiscDatabase {
        return Room.databaseBuilder(application, MiscDatabase::class.java, "MiscDatabase")
//            .addMigrations(Migrations.MIGRATION_40_41(moshi))
            .addTypeConverter(roomWithCategoriesTypeConverter)
            .fallbackToDestructiveMigration()
            .build()
    }
}