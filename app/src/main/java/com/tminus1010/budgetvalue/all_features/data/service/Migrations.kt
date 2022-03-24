package com.tminus1010.budgetvalue.all_features.data.service

import android.annotation.SuppressLint
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types


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

//    val migration_43_44 =
//        object : Migration(43, 44) {
//            @SuppressLint("Range")
//            override fun migrate(database: SupportSQLiteDatabase) {
//                val cursor = database.query("SELECT searchTexts FROM BasicReplayDTO")
//                cursor.moveToFirst()
//                while (!cursor.isAfterLast) {
//                    cursor.getString(cursor.getColumnIndex("searchTexts"))
//                    cursor.moveToNext()
//                }
//            }
//        }

//        BasicFutureDTO()
//        //
//        val name: String,
//        val searchTexts: List<String>,
//        val categoryAmountFormulasStr: String,
//        val autoFillCategoryName: String,
//        val terminationStatus: TerminationStatus,

    fun z43_44(moshi: Moshi) =
        object : Migration(43, 44) {
            @SuppressLint("Range")
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE BasicFutureDTO_new (name TEXT, searchTexts TEXT, categoryAmountFormulasStr TEXT, autoFillCategoryName TEXT, terminationStatus TEXT, PRIMARY KEY(name))")
                database.execSQL("INSERT INTO BasicFutureDTO_new (name, categoryAmountFormulasStr, autoFillCategoryName, terminationStatus) SELECT name, categoryAmountFormulasStr, autoFillCategoryName, terminationStatus FROM BasicFutureDTO")
                database.execSQL("ALTER TABLE BasicFutureDTO_new ADD COLUMN searchTexts TEXT");
                val cursor = database.query("SELECT searchText FROM BasicFutureDTO")
                while (!cursor.isLast) {
                    cursor.moveToNext()
                    val columnName = cursor.run { getString(getColumnIndex("name")) }
                    val searchText = cursor.run { getString(getColumnIndex("searchText")) }
                    val type = Types.newParameterizedType(List::class.java, String::class.java)
                    database.query("UPDATE BasicFutureDTO_new SET searchTexts = ${moshi.adapter<List<String>>(type).toJson(listOf(columnName))} WHERE name = $searchText")
                }
                cursor.close()
                database.execSQL("DROP TABLE BasicFutureDTO")
                database.execSQL("ALTER TABLE BasicFutureDTO_new RENAME TO BasicFutureDTO")
            }
        }
}