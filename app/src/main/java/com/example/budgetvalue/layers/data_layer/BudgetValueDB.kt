package com.example.budgetvalue.layers.data_layer

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.budgetvalue.models.Account
import com.example.budgetvalue.models.Transaction

@Database(entities = [Transaction::class, Account::class], version = 6)
@TypeConverters(MyTypeConverters::class)
abstract class BudgetValueDB : RoomDatabase() {
    abstract fun myDao(): MyDao
}