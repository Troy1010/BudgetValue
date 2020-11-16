package com.example.budgetvalue.layer_data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MyTypeConverters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromBigDecimalToString(x: BigDecimal): String {
            return x.toString()
        }

        @TypeConverter
        @JvmStatic
        fun toBigDecimal(s: String): BigDecimal {
            return s.toBigDecimal()
        }

        @TypeConverter
        @JvmStatic
        fun fromCategoryAmountsToString(x: HashMap<String, BigDecimal>): String {
            return Gson().toJson(x)
        }

        @TypeConverter
        @JvmStatic
        fun toCategoryAmounts(s: String): HashMap<String, BigDecimal> {
            val type: Type = object : TypeToken<HashMap<String, BigDecimal>>() {}.type
            return Gson().fromJson<HashMap<String, BigDecimal>>(s, type)
        }

        @TypeConverter
        @JvmStatic
        fun fromDateToString(x: LocalDate): String {
            return x.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
        }

        @TypeConverter
        @JvmStatic
        fun fromStringToDate(s: String): LocalDate {
            return LocalDate.parse(s, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
        }
    }
}