package com.tminus1010.buva.domain

import java.time.LocalDate

fun String.toLocalDate() =
    LocalDate.parse(this)

fun LocalDate.toPeriod(days: Long) =
    LocalDatePeriod(this, this.plusDays(days))