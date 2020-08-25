package com.example.budgetvalue.layers.data_layer

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.math.BigDecimal

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
    }
}