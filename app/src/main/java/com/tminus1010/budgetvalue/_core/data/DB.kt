package com.tminus1010.budgetvalue._core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tminus1010.budgetvalue.accounts.AccountDTO
import com.tminus1010.budgetvalue.categories.CategoryDTO
import com.tminus1010.budgetvalue.plans.PlanDTO
import com.tminus1010.budgetvalue.reconciliations.ReconciliationDTO
import com.tminus1010.budgetvalue.transactions.TransactionDTO

@TypeConverters(RoomTypeConverter::class)
@Database(entities = [TransactionDTO::class, AccountDTO::class, ReconciliationDTO::class, PlanDTO::class, CategoryDTO::class],
    version = 32)
abstract class DB : RoomDatabase() {
    abstract fun miscDAO(): MiscDAO
    abstract fun activeCategoryDAO(): UserCategoriesDAO
}