package com.tminus1010.budgetvalue.dependency_injection

import androidx.room.Room
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.layer_data.BudgetValueDB
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BudgetValueDBModuleMock {
    @Provides
    @Singleton
    fun providesDatabase(app: App): BudgetValueDB {
        return Room
            .inMemoryDatabaseBuilder(app, BudgetValueDB::class.java) // inMemoryDatabaseBuilder does not write to device
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
}