package com.tminus1010.budgetvalue._core.all.dependency_injection

import android.app.Application
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue._core.data.MiscDatabase
import com.tminus1010.budgetvalue._core.data.RoomTypeConverter
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
    fun providesDatabase(application: Application, moshi: Moshi): MiscDatabase {
        return Room.databaseBuilder(application, MiscDatabase::class.java, "BudgetValueDB")
//            .addMigrations(Migrations.MIGRATION_40_41(moshi))
            .addTypeConverter(RoomTypeConverter(moshi))
            .fallbackToDestructiveMigration()
            .build()
    }
}