package com.tminus1010.buva.environment.adapter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.CategoryAmountFormulas
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import dagger.Reusable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@ProvidedTypeConverter
@Reusable
class RoomWithCategoriesTypeConverter @Inject constructor(
    private val moshiWithCategoriesProvider: MoshiWithCategoriesProvider,
) {
    /**
     * As far as I know, Room does not expose any way for runtime-mutable adapters to cause values to be re-emitted.
     * So, we'll need to do something to cause the values to be re-emitted when moshiWithCategoriesProvider changes, like: .redoWhen(moshiWithCategoriesProvider.moshiFlow)
     */
    private val moshi get() = runBlocking { moshiWithCategoriesProvider.moshiFlow.first() }

    @TypeConverter
    fun toJson(x: Category): String =
        moshi.toJson(x)

    @TypeConverter
    fun fromJson2(s: String): Category =
        moshi.fromJson(s)

    @TypeConverter
    fun toJson(x: CategoryAmounts): String =
        moshi.toJson(x)

    @TypeConverter
    fun fromJson3(s: String): CategoryAmounts =
        moshi.fromJson(s)

    @TypeConverter
    fun toJson(x: CategoryAmountFormulas): String =
        moshi.toJson(x)

    @TypeConverter
    fun fromJson4(s: String): CategoryAmountFormulas =
        moshi.fromJson(s)
}