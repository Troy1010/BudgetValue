package com.tminus1010.budgetvalue._core.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.moshi.Moshi
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson


object Migrations {
    fun MIGRATION_40_41(moshi: Moshi) = object : Migration(40, 41) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE BasicReplay_new (name TEXT, searchText TEXT, categoryAmountFormulas TEXT, autoFillCategory TEXT, PRIMARY KEY(name))")
            database.execSQL("INSERT INTO BasicReplay_new (name, categoryAmountFormulas, autoFillCategory) SELECT name, categoryAmountFormulas, autoFillCategory FROM BasicReplay")
            database.execSQL("ALTER TABLE BasicReplay_new ADD COLUMN searchTexts TEXT");
            val cursor = database.query("SELECT searchText FROM BasicReplay")
            while (!cursor.isLast)
                cursor
                    .apply { moveToNext() }
                    .run { Pair(getString(getColumnIndex("name")), getString(getColumnIndex("searchText"))) }
                    .let { database.query("UPDATE BasicReplay_new SET searchTexts = ${moshi.toJson(listOf(it.second))} WHERE name = ${it.first}") }
            cursor.close()
            database.execSQL("DROP TABLE BasicReplay")
            database.execSQL("ALTER TABLE BasicReplay_new RENAME TO BasicReplay")
        }
    }
}