package com.tminus1010.budgetvalue.layer_data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tminus1010.budgetvalue.model_data.Account
import com.tminus1010.budgetvalue.model_data.PlanCategoryAmount
import com.tminus1010.budgetvalue.model_data.ReconciliationReceived
import com.tminus1010.budgetvalue.model_data.TransactionReceived

@TypeConverters(TypeConverterForRoom::class)
@Database(entities = [TransactionReceived::class, Account::class, ReconciliationReceived::class, PlanCategoryAmount::class], version = 18)
abstract class BudgetValueDB : RoomDatabase() {
    abstract fun myDao(): MyDao
}