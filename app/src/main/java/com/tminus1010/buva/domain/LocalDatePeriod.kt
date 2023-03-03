package com.tminus1010.buva.domain

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

// TODO: LocalDatePeriod could probably be replaced by Period.
@Parcelize
data class LocalDatePeriod(
    val startDate: LocalDate,
    val endDate: LocalDate,
) : Parcelable {
    init {
        if (endDate < startDate) error("endDate < startDate is illegal. Perhaps this could be supported? startDate:$startDate endDate:$endDate")
    }

    constructor(startDate: LocalDate, period: Period) : this(startDate, startDate.plus(period))

    @IgnoredOnParcel
    private val period = Period.between(startDate, endDate)

    val days get() = period.days
    val midDate get() = startDate.plus(period.minusDays(period.days.toLong() / 2))

    operator fun contains(period: LocalDatePeriod): Boolean {
        return period.startDate in this && period.endDate in this
    }

    operator fun contains(localDate: LocalDate): Boolean {
        return (localDate.isAfter(startDate) || localDate == startDate) &&
                (localDate.isBefore(endDate) || localDate == endDate)
    }

    fun toDisplayStr(): String {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
        return "${formatter.format(startDate)} - ${formatter.format(endDate)}"
    }
}