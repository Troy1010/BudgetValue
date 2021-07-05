package com.tminus1010.budgetvalue._core.extensions

import com.squareup.moshi.Moshi

inline fun <reified T> Moshi.toJson(x: T): String =
    this.adapter(T::class.java).toJson(x)

inline fun <reified T> Moshi.fromJson(s: String): T =
    this.adapter(T::class.java).fromJson(s)!!