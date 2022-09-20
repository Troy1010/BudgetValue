package com.tminus1010.buva.domain

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

data class LocalDatePeriod(
    val startDate: LocalDate,
    val endDate: LocalDate,
) {
    init {
        if (endDate < startDate) error("endDate < startDate is illegal. Perhaps this could be supported?")
    }

    constructor(startDate: LocalDate, period: Period) : this(startDate, startDate.plus(period))

    val days get() = ChronoUnit.DAYS.between(startDate, endDate)

    operator fun contains(localDate: LocalDate): Boolean {
        return (localDate.isAfter(startDate) || localDate == startDate) &&
                (localDate.isBefore(endDate) || localDate == endDate)
    }

    fun toDisplayStr(): String {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
        return "${formatter.format(startDate)} - ${formatter.format(endDate)}"
    }
}