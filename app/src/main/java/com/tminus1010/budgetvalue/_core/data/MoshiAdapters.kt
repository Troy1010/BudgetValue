package com.tminus1010.budgetvalue._core.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.tminus1010.budgetvalue.categories.models.CategoryType
import com.tminus1010.budgetvalue.replay_or_future.models.TerminationStatus
import java.math.BigDecimal

object MoshiAdapters {
    /**
     * [BigDecimal]
     */
    @ToJson
    fun toJson(x: BigDecimal): String =
        x.toString()

    @FromJson
    fun fromJson1(s: String): BigDecimal =
        s.toBigDecimal()

    /**
     * [CategoryType]
     */
    @ToJson
    fun toJson(x: CategoryType): String =
        x.ordinal.toString()

    @FromJson
    fun fromJson2(s: String): CategoryType =
        CategoryType.values()[s.toInt()]
}