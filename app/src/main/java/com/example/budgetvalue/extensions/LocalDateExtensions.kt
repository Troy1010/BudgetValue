package com.example.budgetvalue.extensions

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters


fun LocalDate.previous(dayOfWeek: DayOfWeek): LocalDate {
    return this.with(TemporalAdjusters.previous(dayOfWeek))
}
fun LocalDate.previousOrSame(dayOfWeek: DayOfWeek): LocalDate {
    return this.with(TemporalAdjusters.previousOrSame(dayOfWeek))
}

fun LocalDate.next(dayOfWeek: DayOfWeek): LocalDate {
    return this.with(TemporalAdjusters.next(dayOfWeek))
}

fun LocalDate.nextOrSame(dayOfWeek: DayOfWeek): LocalDate {
    return this.with(TemporalAdjusters.nextOrSame(dayOfWeek))
}