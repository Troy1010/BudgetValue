package com.tminus1010.budgetvalue._core.dependency_injection

import androidx.room.Room
import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue._core.App
import com.tminus1010.budgetvalue._core.data.DB
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
    fun providesDatabase(app: App, moshi: Moshi): DB {
        return Room.databaseBuilder(app, DB::class.java, "BudgetValueDB")
            .addTypeConverter(RoomTypeConverter(moshi))
            .fallbackToDestructiveMigration()
            .build()
    }
}