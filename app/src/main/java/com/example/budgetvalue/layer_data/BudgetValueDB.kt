package com.example.budgetvalue.layer_data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.budgetvalue.model_data.Account
import com.example.budgetvalue.model_data.PlanCategoryAmount
import com.example.budgetvalue.model_data.ReconcileCategoryAmount
import com.example.budgetvalue.model_data.TransactionReceived

@Database(entities = [TransactionReceived::class, Account::class, ReconcileCategoryAmount::class, PlanCategoryAmount::class], version = 17)
@TypeConverters(MyTypeConverters::class)
abstract class BudgetValueDB : RoomDatabase() {
    abstract fun myDao(): MyDao
}