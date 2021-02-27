package com.tminus1010.budgetvalue.dependency_injection

import androidx.room.Room
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.layer_data.DB
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepoRoomDBModuleMock {
    @Provides
    @Singleton
    fun providesDatabase(app: App): DB {
        return Room
            .inMemoryDatabaseBuilder(app, DB::class.java) // inMemoryDatabaseBuilder does not write to device
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
}