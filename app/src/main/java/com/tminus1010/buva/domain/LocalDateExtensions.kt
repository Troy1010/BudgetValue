package com.tminus1010.buva.domain

import java.time.LocalDate

fun String.toLocalDate() =
    runCatching { LocalDate.parse(this) }
        .getOrElse { LocalDate.parse(this.split("-").joinToString("-") { if (it.count() == 1) "0$it" else it }) }


fun LocalDate.toPeriod(days: Long) =
    LocalDatePeriod(this, this.plusDays(days))