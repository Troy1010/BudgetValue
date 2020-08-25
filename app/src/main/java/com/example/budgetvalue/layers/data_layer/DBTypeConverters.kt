package com.example.budgetvalue.layers.data_layer

import androidx.room.TypeConverter
import java.math.BigDecimal

class DBTypeConverters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun toBigDecimal(s: String): BigDecimal {
            return s.toBigDecimal()
        }

        @TypeConverter
        @JvmStatic
        fun toStorableString(x: BigDecimal): String {
            return x.toString()
        }
    }
}