package com.tminus1010.budgetvalue.layer_data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tminus1010.budgetvalue.features.accounts.AccountDTO
import com.tminus1010.budgetvalue.features.categories.CategoryDTO
import com.tminus1010.budgetvalue.features.plans.PlanDTO
import com.tminus1010.budgetvalue.features.reconciliations.ReconciliationDTO
import com.tminus1010.budgetvalue.features.transactions.TransactionDTO

@TypeConverters(RoomTypeConverter::class)
@Database(entities = [TransactionDTO::class, AccountDTO::class, ReconciliationDTO::class, PlanDTO::class, CategoryDTO::class],
    version = 32)
abstract class DB : RoomDatabase() {
    abstract fun miscDAO(): MiscDAO
    abstract fun activeCategoryDAO(): UserCategoriesDAO
}