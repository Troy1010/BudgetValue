package com.tminus1010.budgetvalue.data.service

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tminus1010.budgetvalue.domain.Category

@TypeConverters(RoomTypeConverter::class)
@Database(
    version = 1,
    entities = [Category::class],
)
abstract class CategoryDatabase : RoomDatabase() {
    abstract fun userCategoriesDAO(): UserCategoriesDAO
}