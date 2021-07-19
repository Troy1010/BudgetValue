package com.tminus1010.budgetvalue._core.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.moshi.Moshi
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson


object Migrations {
//    fun MIGRATION_40_41(moshi: Moshi) = object : Migration(40, 41) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.execSQL("CREATE TABLE BasicReplayDTO_new (name TEXT, searchText TEXT, categoryAmountFormulasStr TEXT, autoFillCategory TEXT, PRIMARY KEY(name))")
//            database.execSQL("INSERT INTO BasicReplayDTO_new (name, categoryAmountFormulasStr, autoFillCategory) SELECT name, categoryAmountFormulas, autoFillCategory FROM BasicReplayDTO")
//            database.execSQL("ALTER TABLE BasicReplayDTO_new ADD COLUMN searchTexts TEXT");
//            val cursor = database.query("SELECT searchText FROM BasicReplayDTO")
//            while (!cursor.isLast)
//                cursor
//                    .apply { moveToNext() }
//                    .run { Pair(getString(getColumnIndex("name")), getString(getColumnIndex("searchText"))) }
//                    .let { database.query("UPDATE BasicReplayDTO_new SET searchTexts = ${moshi.toJson(listOf(it.second))} WHERE name = ${it.first}") }
//            cursor.close()
//            database.execSQL("DROP TABLE BasicReplayDTO")
//            database.execSQL("ALTER TABLE BasicReplayDTO_new RENAME TO BasicReplayDTO")
//        }
//    }
}