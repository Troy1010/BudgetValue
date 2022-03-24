package com.tminus1010.budgetvalue.all_features.data.service

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.tminus1010.budgetvalue.all_features.domain.CategoryAmountFormulas
import com.tminus1010.budgetvalue.all_features.domain.CategoryAmounts
import com.tminus1010.budgetvalue.all_features.domain.Category
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import dagger.Reusable
import javax.inject.Inject

@ProvidedTypeConverter
@Reusable
class RoomWithCategoriesTypeConverter @Inject constructor(
    private val moshiWithCategoriesProvider: MoshiWithCategoriesProvider,
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
    fun fromJson3(s: String): CategoryAmounts =
        moshiWithCategoriesProvider.moshi.fromJson(s)

    @TypeConverter
    fun toJson(x: CategoryAmountFormulas): String =
        moshiWithCategoriesProvider.moshi.toJson(x)

    @TypeConverter
    fun fromJson4(s: String): CategoryAmountFormulas =
        moshiWithCategoriesProvider.moshi.fromJson(s)
}