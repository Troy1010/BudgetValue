package com.tminus1010.budgetvalue.layer_data

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue.extensions.fromJson
import com.tminus1010.budgetvalue.extensions.toJson
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@ProvidedTypeConverter
class RoomTypeConverter(val moshi: Moshi) {
    @TypeConverter
    fun fromBigDecimalToString(x: BigDecimal): String =
        x.toString()

    @TypeConverter
    fun toBigDecimal(s: String): BigDecimal =
        s.toBigDecimal()

    @TypeConverter
    fun fromCategoryAmountsToString(x: Map<String, BigDecimal>): String =
        moshi.toJson(x)

    @TypeConverter
    fun toCategoryAmounts(s: String): HashMap<String, BigDecimal> =
        moshi.fromJson(s)

    @TypeConverter
    fun fromDateToString(x: LocalDate): String =
        x.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))

    @TypeConverter
    fun fromStringToDate(s: String): LocalDate =
        LocalDate.parse(s, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
}