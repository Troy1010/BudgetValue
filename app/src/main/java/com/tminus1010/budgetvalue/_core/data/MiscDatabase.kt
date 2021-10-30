package com.tminus1010.budgetvalue._core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tminus1010.budgetvalue.accounts.data.AccountDTO
import com.tminus1010.budgetvalue.categories.models.CategoryDTO
import com.tminus1010.budgetvalue.plans.data.model.PlanDTO
import com.tminus1010.budgetvalue.reconcile.data.model.ReconciliationDTO
import com.tminus1010.budgetvalue.replay_or_future.data.model.BasicFutureDTO
import com.tminus1010.budgetvalue.replay_or_future.data.model.BasicReplayDTO
import com.tminus1010.budgetvalue.replay_or_future.data.model.TotalFutureDTO
import com.tminus1010.budgetvalue.transactions.data.TransactionDTO

@TypeConverters(RoomTypeConverter::class)
@Database(
    version = 43,
    entities = [TransactionDTO::class, AccountDTO::class, ReconciliationDTO::class, PlanDTO::class, CategoryDTO::class, BasicReplayDTO::class, BasicFutureDTO::class, TotalFutureDTO::class],
//    autoMigrations = [AutoMigration(from = 41, to = 42)]
)
abstract class MiscDatabase : RoomDatabase() {
    abstract fun miscDAO(): MiscDAO
    abstract fun userCategoriesDAO(): UserCategoriesDAO
    abstract fun userCategoriesDAO2(): UserCategoriesDAO2

    // # Migrations
//    @RenameColumn(tableName = "BasicReplayDTO", fromColumnName = "description", toColumnName = "searchText")
//    class AutoMigration38 : AutoMigrationSpec
}