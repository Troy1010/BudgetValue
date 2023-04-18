package com.tminus1010.buva.environment.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.tminus1010.buva.domain.ResetStrategy
import java.math.BigDecimal

object ResetStrategyAdapter {
    @ToJson
    fun toJson(x: ResetStrategy): String =
        when (x) {
            is ResetStrategy.Basic -> x.budgetedMax.toString()
        }

    @FromJson
    fun fromJson(s: String): ResetStrategy? =
        if (s == "null")
            null
        else
            ResetStrategy.Basic(runCatching { s.toBigDecimal() }.getOrDefault(BigDecimal.ZERO))
}