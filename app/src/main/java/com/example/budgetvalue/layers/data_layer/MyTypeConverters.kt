package com.example.budgetvalue.layers.data_layer

import androidx.room.TypeConverter
import com.example.budgetvalue.layers.view_models.categoriesVM
import com.example.budgetvalue.models.Category
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.math.BigDecimal
import java.text.DateFormat
import java.text.SimpleDateFormat
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
        fun fromCategoryToString(x: Category): String {
            return x.name
        }

        @TypeConverter
        @JvmStatic
        fun fromStringToCategory(s: String): Category {
            return categoriesVM.getCategoryByName(s)
        }

        @TypeConverter
        @JvmStatic
        fun fromDateToString(x: Date): String {
            return SimpleDateFormat("MM/dd/yyyy").format(x)
        }

        @TypeConverter
        @JvmStatic
        fun fromStringToDate(s: String): Date {
            return SimpleDateFormat("MM/dd/yyyy").parse(s)!!
        }
    }
}