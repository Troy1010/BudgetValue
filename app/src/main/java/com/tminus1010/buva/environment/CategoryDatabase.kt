package com.tminus1010.buva.environment

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tminus1010.buva.domain.Category

@TypeConverters(RoomTypeConverter::class)
@Database(
    version = 5,
    entities = [Category::class],
)
abstract class CategoryDatabase : RoomDatabase() {
    abstract fun userCategoriesDAO(): UserCategoriesDAO
}