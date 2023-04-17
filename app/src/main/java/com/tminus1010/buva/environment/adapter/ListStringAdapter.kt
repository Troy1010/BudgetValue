package com.tminus1010.buva.environment.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types

object ListStringAdapter {
    @ToJson
    fun toJson(x: List<String>): String {
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        return MoshiProvider.moshi.adapter<List<String>>(type).toJson(x)
    }

    @FromJson
    fun fromJson6(s: String): List<String>? {
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        return MoshiProvider.moshi.adapter<List<String>>(type).fromJson(s)
    }
}