package com.tminus1010.budgetvalue.layer_data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tminus1010.budgetvalue.model_data.*

@TypeConverters(RoomTypeConverter::class)
@Database(entities = [TransactionReceived::class, AccountDTO::class, ReconciliationReceived::class, PlanCategoryAmount::class, PlanReceived::class, Category::class], version = 27)
abstract class DB : RoomDatabase() {
    abstract fun miscDAO(): MiscDAO
    abstract fun activeCategoryDAO(): ActiveCategoriesDAO
}