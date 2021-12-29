package com.tminus1010.budgetvalue._core.data

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.tminus1010.budgetvalue._core.domain.CategoryAmounts
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import javax.inject.Inject

@ProvidedTypeConverter
class RoomWithCategoriesTypeConverter @Inject constructor(
    private val moshiWithCategoriesProvider: MoshiWithCategoriesProvider
) {
    @TypeConverter
    fun toJson(x: Category): String =
        moshiWithCategoriesProvider.moshi.toJson(x)

    @TypeConverter
    fun fromJson2(s: String): Category =
        moshiWithCategoriesProvider.moshi.fromJson(s)

    @TypeConverter
    fun toJson(x: CategoryAmounts): String =
        moshiWithCategoriesProvider.moshi.toJson(x)

    @TypeConverter
    fun toCategoryAmounts(s: String): CategoryAmounts =
        moshiWithCategoriesProvider.moshi.fromJson(s)
}