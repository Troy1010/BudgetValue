package com.tminus1010.budgetvalue.aa_core.middleware

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

data class LocalDatePeriod(
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    constructor(startDate: LocalDate, period: Period) : this(startDate, startDate.plus(period))

    operator fun contains(localDate: LocalDate): Boolean {
        return (localDate.isAfter(startDate) || localDate == startDate) &&
                (localDate.isBefore(endDate) || localDate == endDate)
    }

    fun toDisplayStr(): String {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
        return "${formatter.format(startDate)} - ${formatter.format(endDate)}"
    }
}