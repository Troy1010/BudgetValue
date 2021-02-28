package com.tminus1010.budgetvalue

import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tminus1010.budgetvalue.model_domain.Category
import java.math.BigDecimal

private object Adapters {
    @FromJson
    fun fromJson1(s: String): BigDecimal =
        s.toBigDecimal()

    @ToJson
    fun toJson(x: BigDecimal): String =
        x.toString()

    @FromJson
    fun fromJson2(s: String): Category.Type =
        Category.Type.values()[s.toInt()]

    @ToJson
    fun toJson(x: Category.Type): String =
        x.ordinal.toString()
}

// TODO("Use dagger")
val moshi = Moshi.Builder()
    .add(Adapters)
    .addLast(KotlinJsonAdapterFactory())
    .build()