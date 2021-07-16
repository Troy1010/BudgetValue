package com.tminus1010.budgetvalue._core.data

import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.tminus1010.budgetvalue.accounts.models.AccountDTO
import com.tminus1010.budgetvalue.categories.models.CategoryDTO
import com.tminus1010.budgetvalue.plans.models.PlanDTO
import com.tminus1010.budgetvalue.reconciliations.models.ReconciliationDTO
import com.tminus1010.budgetvalue.replay_or_future.models.BasicFutureDTO
import com.tminus1010.budgetvalue.replay_or_future.models.BasicReplayDTO
import com.tminus1010.budgetvalue.transactions.models.TransactionDTO

@TypeConverters(RoomTypeConverter::class)
@Database(
    version = 40,
    entities = [TransactionDTO::class, AccountDTO::class, ReconciliationDTO::class, PlanDTO::class, CategoryDTO::class, BasicReplayDTO::class, BasicFutureDTO::class],
//    autoMigrations = [AutoMigration(from = 38, to = 39, spec = DB.AutoMigration38::class), AutoMigration(from = 39, to = 40, spec = DB.AutoMigration38::class)]
)
abstract class DB : RoomDatabase() {
    abstract fun miscDAO(): MiscDAO
    abstract fun userCategoriesDAO(): UserCategoriesDAO

    // # Migrations
//    @RenameColumn(tableName = "BasicReplayDTO", fromColumnName = "description", toColumnName = "searchText")
//    class AutoMigration38 : AutoMigrationSpec
}