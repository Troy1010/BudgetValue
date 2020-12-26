package com.tminus1010.budgetvalue.extensions

import org.junit.Test

import org.junit.Assert.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

class LocalDateKtTest {

    @Test
    fun previous() {
        // # Given
        val date1 = LocalDate.of(2020, Month.FEBRUARY, 7)
        val date2 = LocalDate.of(2020, Month.FEBRUARY, 8)
        val date3 = LocalDate.of(2020, Month.FEBRUARY, 9)
        // # Stimulate & Verify
        assertEquals(LocalDate.of(2020, Month.FEBRUARY, 1), date1.previous(DayOfWeek.SATURDAY))
        assertEquals(LocalDate.of(2020, Month.FEBRUARY, 1), date2.previous(DayOfWeek.SATURDAY))
        assertEquals(LocalDate.of(2020, Month.FEBRUARY, 8), date3.previous(DayOfWeek.SATURDAY))
    }

    @Test
    operator fun next() {
        // # Given
        val date1 = LocalDate.of(2020, Month.FEBRUARY, 7)
        val date2 = LocalDate.of(2020, Month.FEBRUARY, 8)
        val date3 = LocalDate.of(2020, Month.FEBRUARY, 9)
        // # Stimulate & Verify
        assertEquals(LocalDate.of(2020, Month.FEBRUARY, 8), date1.next(DayOfWeek.SATURDAY))
        assertEquals(LocalDate.of(2020, Month.FEBRUARY, 15), date2.next(DayOfWeek.SATURDAY))
        assertEquals(LocalDate.of(2020, Month.FEBRUARY, 15), date3.next(DayOfWeek.SATURDAY))
    }
}