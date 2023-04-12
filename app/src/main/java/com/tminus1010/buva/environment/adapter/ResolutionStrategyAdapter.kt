package com.tminus1010.buva.environment.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.tminus1010.buva.domain.ResolutionStrategy
import java.math.BigDecimal

object ResolutionStrategyAdapter {
    @ToJson
    fun toJson(x: ResolutionStrategy): String =
        when (x) {
            is ResolutionStrategy.Basic -> x.budgetedMin.toString()
            is ResolutionStrategy.MatchPlan -> "MatchPlan"
        }

    @FromJson
    fun fromJson21(s: String): ResolutionStrategy =
        when (s) {
            "null" -> ResolutionStrategy.Basic(null)
            "MatchPlan" -> ResolutionStrategy.MatchPlan
            else -> ResolutionStrategy.Basic(runCatching { s.toBigDecimal() }.getOrDefault(BigDecimal.ZERO))
        }
}