package com.tminus1010.budgetvalue._core.dependency_injection

import android.app.Application
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue._core.data.DB
import com.tminus1010.budgetvalue._core.data.Migrations
import com.tminus1010.budgetvalue._core.data.RoomTypeConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoRoomDBModule {
    @Provides
    @Singleton
    fun providesDatabase(application: Application, moshi: Moshi): DB {
        return Room.databaseBuilder(application, DB::class.java, "BudgetValueDB")
//            .addMigrations(Migrations.MIGRATION_40_41(moshi))
            .addTypeConverter(RoomTypeConverter(moshi))
            .fallbackToDestructiveMigration()
            .build()
    }
}