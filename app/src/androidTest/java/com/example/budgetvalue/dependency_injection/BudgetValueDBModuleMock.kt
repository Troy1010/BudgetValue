package com.example.budgetvalue.dependency_injection

import android.app.Application
import androidx.room.Room
import com.example.budgetvalue.layer_data.BudgetValueDB
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BudgetValueDBModuleMock {
    @Provides
    @Singleton
    fun providesDatabase(app: Application): BudgetValueDB {
        return Room
            .inMemoryDatabaseBuilder(app, BudgetValueDB::class.java) // inMemoryDatabaseBuilder does not write to device
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
}