package com.tminus1010.budgetvalue.all_features.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tminus1010.budgetvalue.all_features.domain.accounts.Account
import com.tminus1010.budgetvalue.all_features.domain.plan.Plan
import com.tminus1010.budgetvalue.reconcile.data.model.ReconciliationDTO
import com.tminus1010.budgetvalue.replay_or_future.data.model.BasicReplayDTO
import com.tminus1010.budgetvalue.replay_or_future.domain.BasicFuture
import com.tminus1010.budgetvalue.replay_or_future.domain.TotalFuture
import com.tminus1010.budgetvalue.transactions.data.TransactionDTO

@TypeConverters(RoomTypeConverter::class, RoomWithCategoriesTypeConverter::class)
@Database(
    version = 45,
    entities = [TransactionDTO::class, Account::class, ReconciliationDTO::class, BasicReplayDTO::class, BasicFuture::class, TotalFuture::class, Plan::class],
//    autoMigrations = [AutoMigration(from = 41, to = 42)]
)
abstract class MiscDatabase : RoomDatabase() {
    abstract fun miscDAO(): MiscDAO

    // # Migrations
//    @RenameColumn(tableName = "BasicReplayDTO", fromColumnName = "description", toColumnName = "searchText")
//    class AutoMigration38 : AutoMigrationSpec
}