package com.tminus1010.buva.environment.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object LocalDateAdapter {
    private val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

    @ToJson
    fun toJson(x: LocalDate?): String =
        x?.format(dateFormatter).toString()

    @FromJson
    fun fromJson(s: String): LocalDate? =
        when (s) {
            "null" -> null
            else -> s.let { LocalDate.parse(s, dateFormatter) }
        }
}