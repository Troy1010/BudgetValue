package com.example.budgetvalue.dependency_injection

import androidx.room.Room
import com.example.budgetvalue.App
import com.example.budgetvalue.layer_data.BudgetValueDB
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BudgetValueDBModule {
    @Provides
    @Singleton
    fun providesDatabase(app: App): BudgetValueDB {
        return Room.databaseBuilder(app, BudgetValueDB::class.java, "BudgetValueDB")
            .fallbackToDestructiveMigration()
            .build()
    }
}