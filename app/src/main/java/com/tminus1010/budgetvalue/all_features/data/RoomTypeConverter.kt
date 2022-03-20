package com.tminus1010.budgetvalue.all_features.data

import androidx.room.TypeConverter
import com.tminus1010.budgetvalue.all_features.data.MoshiProvider.moshi
import com.tminus1010.budgetvalue.all_features.domain.LocalDatePeriod
import com.tminus1010.budgetvalue.replay_or_future.domain.TerminationStrategy
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import java.math.BigDecimal
import java.time.LocalDate

object RoomTypeConverter {
    @TypeConverter
    fun fromBigDecimalToString(x: BigDecimal): String =
        moshi.toJson(x)

    @TypeConverter
    fun toBigDecimal(s: String): BigDecimal =
        moshi.fromJson(s)

    @TypeConverter
    fun fromDateToString(x: LocalDate?): String? =
        moshi.toJson(x)

    @TypeConverter
    fun fromStringToDate(s: String?): LocalDate? =
        moshi.fromJson(s)

    @TypeConverter
    fun toJson(x: TerminationStrategy): String =
        moshi.toJson(x)

    @TypeConverter
    fun fromJson(s: String): TerminationStrategy? =
        moshi.fromJson(s)

    @TypeConverter
    fun toJson(x: AmountFormula): String =
        moshi.toJson(x)

    @TypeConverter
    fun fromJson2(s: String): AmountFormula? =
        moshi.fromJson(s)

    @TypeConverter
    fun fromLocalDatePeriod(x: LocalDatePeriod): String =
        moshi.toJson(x)

    @TypeConverter
    fun toLocalDatePeriod(s: String): LocalDatePeriod? =
        moshi.fromJson(s)

    @TypeConverter
    fun fromListOfString(x: List<String>): String =
        moshi.toJson(x)

    @TypeConverter
    fun toListOfString(s: String): List<String>? =
        moshi.fromJson(s)
}