package com.tminus1010.budgetvalue._core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tminus1010.budgetvalue.accounts.app.Account
import com.tminus1010.budgetvalue.categories.models.CategoryDTO
import com.tminus1010.budgetvalue.plans.data.model.PlanDTO
import com.tminus1010.budgetvalue.plans.domain.Plan
import com.tminus1010.budgetvalue.reconcile.data.model.ReconciliationDTO
import com.tminus1010.budgetvalue.replay_or_future.data.model.BasicReplayDTO
import com.tminus1010.budgetvalue.replay_or_future.domain.BasicFuture
import com.tminus1010.budgetvalue.replay_or_future.domain.TotalFuture
import com.tminus1010.budgetvalue.transactions.data.TransactionDTO

@TypeConverters(RoomTypeConverter::class, RoomWithCategoriesTypeConverter::class)
@Database(
    version = 44,
    entities = [TransactionDTO::class, Account::class, ReconciliationDTO::class, PlanDTO::class, CategoryDTO::class, BasicReplayDTO::class, BasicFuture::class, TotalFuture::class, Plan::class],
//    autoMigrations = [AutoMigration(from = 41, to = 42)]
)
abstract class MiscDatabase : RoomDatabase() {
    abstract fun miscDAO(): MiscDAO

    // # Migrations
//    @RenameColumn(tableName = "BasicReplayDTO", fromColumnName = "description", toColumnName = "searchText")
//    class AutoMigration38 : AutoMigrationSpec
}