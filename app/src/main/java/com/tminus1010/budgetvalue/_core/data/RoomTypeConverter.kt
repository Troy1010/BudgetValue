package com.tminus1010.budgetvalue._core.data

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue.replay_or_future.domain.TerminationStatus
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@ProvidedTypeConverter
class RoomTypeConverter(val moshi: Moshi) {
    @TypeConverter
    fun fromBigDecimalToString(x: BigDecimal): String =
        moshi.toJson(x)

    @TypeConverter
    fun toBigDecimal(s: String): BigDecimal =
        moshi.fromJson(s)

    @TypeConverter
    fun fromCategoryAmountsToString(x: Map<String, BigDecimal>): String =
        moshi.toJson(x)

    @TypeConverter
    fun toCategoryAmounts(s: String): HashMap<String, BigDecimal> =
        moshi.fromJson(s)

    @TypeConverter
    fun fromDateToString(x: LocalDate?): String? =
        moshi.toJson(x)

    @TypeConverter
    fun fromStringToDate(s: String?): LocalDate? =
        moshi.fromJson(s)

    @TypeConverter
    fun toJson(x: TerminationStatus): String =
        moshi.toJson(x)

    @TypeConverter
    fun fromJson(s: String): TerminationStatus? =
        moshi.fromJson(s)
}