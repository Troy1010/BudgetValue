package com.tminus1010.budgetvalue.dependency_injection

import androidx.room.Room
import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.layer_data.DB
import com.tminus1010.budgetvalue.layer_data.RoomTypeConverter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepoRoomDBModule {
    @Provides
    @Singleton
    fun providesDatabase(app: App, moshi: Moshi): DB {
        return Room.databaseBuilder(app, DB::class.java, "BudgetValueDB")
            .addTypeConverter(RoomTypeConverter(moshi))
            .fallbackToDestructiveMigration()
            .build()
    }
}