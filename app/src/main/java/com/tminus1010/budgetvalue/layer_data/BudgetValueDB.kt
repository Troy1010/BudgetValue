package com.tminus1010.budgetvalue.layer_data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tminus1010.budgetvalue.model_data.Account
import com.tminus1010.budgetvalue.model_data.PlanCategoryAmount
import com.tminus1010.budgetvalue.model_data.ReconcileCategoryAmount
import com.tminus1010.budgetvalue.model_data.TransactionReceived

@Database(entities = [TransactionReceived::class, Account::class, ReconcileCategoryAmount::class, PlanCategoryAmount::class], version = 17)
@TypeConverters(RoomTypeConverters::class)
abstract class BudgetValueDB : RoomDatabase() {
    abstract fun myDao(): MyDao
}