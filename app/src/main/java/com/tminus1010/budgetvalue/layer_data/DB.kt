package com.tminus1010.budgetvalue.layer_data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tminus1010.budgetvalue.modules.accounts.AccountDTO
import com.tminus1010.budgetvalue.modules.categories.CategoryDTO
import com.tminus1010.budgetvalue.modules.plans.PlanDTO
import com.tminus1010.budgetvalue.modules.reconciliations.ReconciliationDTO
import com.tminus1010.budgetvalue.modules.transactions.TransactionDTO

@TypeConverters(RoomTypeConverter::class)
@Database(entities = [TransactionDTO::class, AccountDTO::class, ReconciliationDTO::class, PlanDTO::class, CategoryDTO::class],
    version = 32)
abstract class DB : RoomDatabase() {
    abstract fun miscDAO(): MiscDAO
    abstract fun activeCategoryDAO(): UserCategoriesDAO
}