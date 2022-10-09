package com.tminus1010.buva.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class ExtensionsKtTest {

    @Test
    fun mergeOverlapping() {
        val date = LocalDate.of(2018, 1, 1)
        val expectedResult = listOf(LocalDatePeriod(date.minusDays(8), date.plusDays(8)))
        listOf(
            LocalDatePeriod(
                date.minusDays(1),
                date.plusDays(8),
            ),
            LocalDatePeriod(
                date.minusDays(8),
                date,
            ),
        )
            .mergeOverlapping()
            .also { assertEquals(expectedResult, it) }
    }
}