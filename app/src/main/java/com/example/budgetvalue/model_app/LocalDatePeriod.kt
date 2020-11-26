package com.example.budgetvalue.model_app

import java.time.LocalDate
import java.time.Period

data class LocalDatePeriod(
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    constructor(startDate: LocalDate, period: Period) : this(startDate, startDate.plus(period))

    operator fun contains(localDate: LocalDate): Boolean {
        return (localDate.isAfter(startDate) || localDate == startDate) &&
                (localDate.isBefore(endDate) || localDate == endDate)
    }
}