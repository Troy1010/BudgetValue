package com.tminus1010.budgetvalue.extensions

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter

@ExperimentalStdlibApi
inline fun <reified T> Moshi.toJson(x: T): String =
    this.adapter<T>().toJson(x)

@ExperimentalStdlibApi
inline fun <reified T> Moshi.fromJson(s: String): T =
    this.adapter<T>().fromJson(s)!!