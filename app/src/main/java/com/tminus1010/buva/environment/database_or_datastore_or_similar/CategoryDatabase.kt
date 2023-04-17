package com.tminus1010.buva.environment.database_or_datastore_or_similar

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.environment.adapter.RoomTypeConverter

@TypeConverters(RoomTypeConverter::class)
@Database(
    version = 7,
    entities = [Category::class],
)
abstract class CategoryDatabase : RoomDatabase() {
    abstract fun userCategoriesDAO(): UserCategoriesDAO
}