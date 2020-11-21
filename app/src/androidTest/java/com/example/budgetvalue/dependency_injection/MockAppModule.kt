package com.example.budgetvalue.dependency_injection

import android.app.Application
import androidx.room.Room
import com.example.budgetvalue.layer_data.BudgetValueDB

class MockAppModule(appProvider: () -> Application) : AppModule(appProvider) {
    override fun providesDatabase(app: Application): BudgetValueDB {
        return Room.inMemoryDatabaseBuilder(app, BudgetValueDB::class.java)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
}