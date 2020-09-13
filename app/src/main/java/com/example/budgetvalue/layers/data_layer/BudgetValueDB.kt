package com.example.budgetvalue.layers.data_layer

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.budgetvalue.models.Account
import com.example.budgetvalue.models.IncomeCategoryAmounts
import com.example.budgetvalue.models.Transaction

@Database(entities = [Transaction::class, Account::class, IncomeCategoryAmounts::class], version = 7)
@TypeConverters(MyTypeConverters::class)
abstract class BudgetValueDB : RoomDatabase() {
    abstract fun myDao(): MyDao
}