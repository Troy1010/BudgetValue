package com.tminus1010.budgetvalue._core.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.categories.models.CategoryType
import java.math.BigDecimal

object MoshiAdapters {
    @ToJson
    fun toJson(x: BigDecimal): String =
        x.toString()

    @FromJson
    fun fromJson1(s: String): BigDecimal =
        s.toBigDecimal()

    @ToJson
    fun toJson(x: CategoryType): String =
        x.ordinal.toString()

    @FromJson
    fun fromJson2(s: String): CategoryType =
        CategoryType.values()[s.toInt()]
}