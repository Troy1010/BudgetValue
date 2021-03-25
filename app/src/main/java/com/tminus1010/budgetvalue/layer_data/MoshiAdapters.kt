package com.tminus1010.budgetvalue.layer_data

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.tminus1010.budgetvalue.features.categories.Category
import java.math.BigDecimal

object MoshiAdapters {
    @ToJson
    fun toJson(x: BigDecimal): String =
        x.toString()

    @FromJson
    fun fromJson1(s: String): BigDecimal =
        s.toBigDecimal()

    @ToJson
    fun toJson(x: Category.Type): String =
        x.ordinal.toString()

    @FromJson
    fun fromJson2(s: String): Category.Type =
        Category.Type.values()[s.toInt()]
}