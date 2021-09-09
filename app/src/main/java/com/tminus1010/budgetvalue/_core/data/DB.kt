package com.tminus1010.budgetvalue._core.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tminus1010.budgetvalue.accounts.models.AccountDTO
import com.tminus1010.budgetvalue.categories.models.CategoryDTO
import com.tminus1010.budgetvalue.plans.models.PlanDTO
import com.tminus1010.budgetvalue.reconciliations.models.ReconciliationDTO
import com.tminus1010.budgetvalue.replay_or_future.models.BasicFutureDTO
import com.tminus1010.budgetvalue.replay_or_future.models.BasicReplayDTO
import com.tminus1010.budgetvalue.replay_or_future.models.TotalFutureDTO
import com.tminus1010.budgetvalue.transactions.models.TransactionDTO

@TypeConverters(RoomTypeConverter::class)
@Database(
    version = 43,
    entities = [TransactionDTO::class, AccountDTO::class, ReconciliationDTO::class, PlanDTO::class, CategoryDTO::class, BasicReplayDTO::class, BasicFutureDTO::class, TotalFutureDTO::class],
//    autoMigrations = [AutoMigration(from = 41, to = 42)]
)
abstract class DB : RoomDatabase() {
    abstract fun miscDAO(): MiscDAO
    abstract fun userCategoriesDAO(): UserCategoriesDAO

    // # Migrations
//    @RenameColumn(tableName = "BasicReplayDTO", fromColumnName = "description", toColumnName = "searchText")
//    class AutoMigration38 : AutoMigrationSpec
}