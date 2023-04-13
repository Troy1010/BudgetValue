package com.tminus1010.buva.domain

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.Month

class LocalDatePeriodTest {

    @Test
    fun contains_GivenWithin() {
        // # Given
        val date1 = LocalDate.of(2020, Month.APRIL, 5)
        val date2 = LocalDate.of(2020, Month.APRIL, 15)
        val date3 = LocalDate.of(2020, Month.APRIL, 28)
        val localDatePeriod = LocalDatePeriod(
            LocalDate.of(2020, Month.APRIL, 5),
            LocalDate.of(2020, Month.APRIL, 28)
        )
        // # Stimulate & Verify
        assertTrue(date1 in localDatePeriod)
        assertTrue(date2 in localDatePeriod)
        assertTrue(date3 in localDatePeriod)
    }

    @Test
    fun contains_GivenWithout() {
        // # Given
        val date1 = LocalDate.of(2020, Month.APRIL, 4)
        val date2 = LocalDate.of(2020, Month.APRIL, 29)
        val localDatePeriod = LocalDatePeriod(
            LocalDate.of(2020, Month.APRIL, 5),
            LocalDate.of(2020, Month.APRIL, 28)
        )
        // # Stimulate & Verify
        assertFalse(date1 in localDatePeriod)
        assertFalse(date2 in localDatePeriod)
    }

    @Test
    fun toDisplayStr() {
        // # Given
        val localDatePeriod = LocalDatePeriod(
            LocalDate.of(2020, Month.APRIL, 5),
            LocalDate.of(2020, Month.APRIL, 28)
        )
        // # Stimulate
        val result = localDatePeriod.toDisplayStr()
        //
        assertEquals("4/5/20 - 4/28/20", result)
    }
}