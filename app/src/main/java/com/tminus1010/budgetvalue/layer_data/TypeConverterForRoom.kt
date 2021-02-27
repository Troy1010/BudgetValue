package com.tminus1010.budgetvalue.layer_data

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tminus1010.budgetvalue.model_data.Category
import java.lang.reflect.Type
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@ProvidedTypeConverter
class TypeConverterForRoom {
    @TypeConverter
    fun fromCategoryTypeToInt(x: Category.Type): Int =
        x.ordinal

    @TypeConverter
    fun fromIntToCategoryType(i: Int): Category.Type =
        Category.Type.values()[i]

    @TypeConverter
    fun fromBigDecimalToString(x: BigDecimal): String {
        return x.toString()
    }

    @TypeConverter
    fun toBigDecimal(s: String): BigDecimal {
        return s.toBigDecimal()
    }

    @TypeConverter
    fun fromCategoryAmountsToString(x: Map<String, BigDecimal>): String {
        return Gson().toJson(x)
    }

    @TypeConverter
    fun toCategoryAmounts(s: String): HashMap<String, BigDecimal> {
        val type: Type = object : TypeToken<HashMap<String, BigDecimal>>() {}.type
        return Gson().fromJson<HashMap<String, BigDecimal>>(s, type)
    }

    @TypeConverter
    fun fromDateToString(x: LocalDate): String {
        return x.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
    }

    @TypeConverter
    fun fromStringToDate(s: String): LocalDate {
        return LocalDate.parse(s, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
    }
}