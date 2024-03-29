package com.tminus1010.buva.environment.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tminus1010.buva.domain.*
import com.tminus1010.buva.environment.adapter.RoomTypeConverter
import com.tminus1010.buva.environment.adapter.RoomWithCategoriesTypeConverter

@TypeConverters(RoomTypeConverter::class, RoomWithCategoriesTypeConverter::class)
@Database(
    version = 57,
    entities = [Future::class, Transaction::class, Account::class, Reconciliation::class, Plan::class, ReconciliationSkip::class, TransactionImportInfo::class, AccountsUpdateInfo::class],
//    autoMigrations = [AutoMigration(from = 41, to = 42)]
)
abstract class MiscDatabase : RoomDatabase() {
    abstract fun miscDAO(): MiscDAO

    // # Migrations
//    @RenameColumn(tableName = "BasicReplayDTO", fromColumnName = "description", toColumnName = "searchText")
//    class AutoMigration38 : AutoMigrationSpec
}