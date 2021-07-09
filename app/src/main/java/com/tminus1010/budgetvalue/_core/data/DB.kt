package com.tminus1010.budgetvalue._core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tminus1010.budgetvalue.accounts.models.AccountDTO
import com.tminus1010.budgetvalue.categories.models.CategoryDTO
import com.tminus1010.budgetvalue.plans.models.PlanDTO
import com.tminus1010.budgetvalue.reconciliations.models.ReconciliationDTO
import com.tminus1010.budgetvalue.replay.models.AutoReplayDTO
import com.tminus1010.budgetvalue.replay.models.BasicReplayDTO
import com.tminus1010.budgetvalue.transactions.models.TransactionDTO

@TypeConverters(RoomTypeConverter::class)
@Database(
    entities = [TransactionDTO::class, AccountDTO::class, ReconciliationDTO::class, PlanDTO::class, CategoryDTO::class, AutoReplayDTO::class, BasicReplayDTO::class],
    version = 35
)
abstract class DB : RoomDatabase() {
    abstract fun miscDAO(): MiscDAO
    abstract fun userCategoriesDAO(): UserCategoriesDAO
}