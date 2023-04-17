package com.tminus1010.buva.environment.adapter

import androidx.room.TypeConverter
import com.tminus1010.buva.domain.*
import com.tminus1010.buva.environment.adapter.MoshiProvider.moshi
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import java.math.BigDecimal
import java.time.LocalDate

object RoomTypeConverter {
    @TypeConverter
    fun toJson(x: BigDecimal?): String? =
        moshi.toJson(x)

    @TypeConverter
    fun toBigDecimal(s: String?): BigDecimal? =
        moshi.fromJson(s)

    @TypeConverter
    fun toJson(x: LocalDate?): String? =
        moshi.toJson(x)

    @TypeConverter
    fun fromStringToDate(s: String?): LocalDate? =
        moshi.fromJson(s)

    @TypeConverter
    fun toJson(x: TerminationStrategy): String =
        moshi.toJson(x)

    @TypeConverter
    fun fromResetStrategyToJson(s: String): TerminationStrategy? =
        moshi.fromJson(s)

    @TypeConverter
    fun toJson(x: ResetStrategy): String =
        moshi.toJson(x)

    @TypeConverter
    fun fromJson432(s: String): ResetStrategy? =
        moshi.fromJson(s)

    @TypeConverter
    fun toJson(x: ResolutionStrategy): String =
        moshi.toJson(x)

    @TypeConverter
    fun fromJson132(s: String): ResolutionStrategy? =
        moshi.fromJson(s)

    @TypeConverter
    fun toJson(x: TransactionMatcher?): String? =
        moshi.toJson(x)

    @TypeConverter
    fun fromJson3(s: String?): TransactionMatcher? =
        moshi.fromJson(s)

    @TypeConverter
    fun toJson(x: AmountFormula): String =
        moshi.toJson(x)

    @TypeConverter
    fun fromJson2(s: String): AmountFormula? =
        moshi.fromJson(s)

    @TypeConverter
    fun toJson(x: LocalDatePeriod): String =
        moshi.toJson(x)

    @TypeConverter
    fun toLocalDatePeriod(s: String): LocalDatePeriod? =
        moshi.fromJson(s)

    @TypeConverter
    fun toJson(x: List<String>): String =
        moshi.toJson(x)

    @TypeConverter
    fun toListOfString(s: String): List<String>? =
        moshi.fromJson(s)

    @TypeConverter
    fun toJson(x: ReconciliationStrategyGroup): String =
        moshi.toJson(x)

    @TypeConverter
    fun fromJson2345(s: String): ReconciliationStrategyGroup? =
        moshi.fromJson(s)
}