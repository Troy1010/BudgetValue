package com.tminus1010.budgetvalue.layer_data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tminus1010.budgetvalue.model_data.*

@TypeConverters(TypeConverterForRoom::class)
@Database(entities = [TransactionReceived::class, Account::class, ReconciliationReceived::class, PlanCategoryAmount::class, PlanReceived::class, Category::class], version = 27)
abstract class BudgetValueDB : RoomDatabase() {
    abstract fun myDao(): MiscDAO
    abstract fun activeCategoryDAO(): ActiveCategoriesDAO
}