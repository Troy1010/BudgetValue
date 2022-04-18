package com.tminus1010.buva.data.service

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.buva.domain.Future
import com.tminus1010.buva.domain.Reconciliation
import com.tminus1010.buva.domain.Account
import com.tminus1010.buva.domain.Plan

@TypeConverters(RoomTypeConverter::class, RoomWithCategoriesTypeConverter::class)
@Database(
    version = 49,
    entities = [Future::class, Transaction::class, Account::class, Reconciliation::class, Plan::class],
//    autoMigrations = [AutoMigration(from = 41, to = 42)]
)
abstract class MiscDatabase : RoomDatabase() {
    abstract fun miscDAO(): MiscDAO

    // # Migrations
//    @RenameColumn(tableName = "BasicReplayDTO", fromColumnName = "description", toColumnName = "searchText")
//    class AutoMigration38 : AutoMigrationSpec
}