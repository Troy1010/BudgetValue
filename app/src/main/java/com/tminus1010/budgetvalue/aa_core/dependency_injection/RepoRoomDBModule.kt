package com.tminus1010.budgetvalue.aa_core.dependency_injection

import androidx.room.Room
import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue.aa_core.App
import com.tminus1010.budgetvalue.aa_core.data.DB
import com.tminus1010.budgetvalue.aa_core.data.RoomTypeConverter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object RepoRoomDBModule {
    @Provides
    @Singleton
    @JvmStatic
    fun providesDatabase(app: App, moshi: Moshi): DB {
        return Room.databaseBuilder(app, DB::class.java, "BudgetValueDB")
            .addTypeConverter(RoomTypeConverter(moshi))
            .fallbackToDestructiveMigration()
            .build()
    }
}