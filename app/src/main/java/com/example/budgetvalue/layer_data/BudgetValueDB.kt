package com.example.budgetvalue.layer_data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.budgetvalue.model_data.Account
import com.example.budgetvalue.model_data.ReconcileCategoryAmounts
import com.example.budgetvalue.model_data.PlanCategoryAmounts
import com.example.budgetvalue.model_app.Transaction

@Database(entities = [Transaction::class, Account::class, ReconcileCategoryAmounts::class, PlanCategoryAmounts::class], version = 13)
@TypeConverters(MyTypeConverters::class)
abstract class BudgetValueDB : RoomDatabase() {
    abstract fun myDao(): MyDao
}