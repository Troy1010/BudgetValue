package com.tminus1010.budgetvalue.dependency_injection

import androidx.room.Room
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.layer_data.DB
import com.tminus1010.budgetvalue.layer_data.TypeConverterForRoom
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepoRoomDBModule {
    @Provides
    @Singleton
    fun providesDatabase(app: App): DB {
        return Room.databaseBuilder(app, DB::class.java, "BudgetValueDB")
            .addTypeConverter(TypeConverterForRoom())
            .fallbackToDestructiveMigration()
            .build()
    }
}