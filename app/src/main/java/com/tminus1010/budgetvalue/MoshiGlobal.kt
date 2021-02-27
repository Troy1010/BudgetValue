package com.tminus1010.budgetvalue

import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import java.math.BigDecimal

private object Adapters {
    @FromJson
    fun fromJson1(s: String): BigDecimal =
        s.toBigDecimal()

    @ToJson
    fun toJson(x: BigDecimal): String =
        x.toString()
}

// TODO("Use dagger")
val moshi = Moshi.Builder()
    .add(Adapters)
    .build()