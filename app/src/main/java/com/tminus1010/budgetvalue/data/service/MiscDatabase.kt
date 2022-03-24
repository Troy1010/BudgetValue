package com.tminus1010.budgetvalue.data.service

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tminus1010.budgetvalue._unrestructured.reconcile.data.model.ReconciliationDTO
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.domain.BasicFuture
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.domain.TotalFuture
import com.tminus1010.budgetvalue._unrestructured.transactions.data.TransactionDTO
import com.tminus1010.budgetvalue.domain.accounts.Account
import com.tminus1010.budgetvalue.domain.plan.Plan

@TypeConverters(RoomTypeConverter::class, RoomWithCategoriesTypeConverter::class)
@Database(
    version = 45,
    entities = [TransactionDTO::class, Account::class, ReconciliationDTO::class, BasicFuture::class, TotalFuture::class, Plan::class],
//    autoMigrations = [AutoMigration(from = 41, to = 42)]
)
abstract class MiscDatabase : RoomDatabase() {
    abstract fun miscDAO(): MiscDAO

    // # Migrations
//    @RenameColumn(tableName = "BasicReplayDTO", fromColumnName = "description", toColumnName = "searchText")
//    class AutoMigration38 : AutoMigrationSpec
}