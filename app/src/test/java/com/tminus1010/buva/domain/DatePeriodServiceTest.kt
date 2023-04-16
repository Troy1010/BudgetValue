package com.tminus1010.buva.domain

import com.tminus1010.buva.ui.review.review_pie_chart.SelectableDuration
import com.tminus1010.buva.ui.review.review_pie_chart.UsePeriodType
import junit.framework.Assert.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class DatePeriodServiceTest {

    @Test
    fun getPeriods() {
        // # Given
        val givenSelectableDuration = SelectableDuration.BY_WEEK
        val givenUsePeriodType = UsePeriodType.USE_DAY_COUNT_PERIODS
        val givenDatePeriod = LocalDatePeriod(startDate = LocalDate.parse("2023-01-01"), endDate = LocalDate.parse("2023-01-15"))
        val expected =
            listOf(
                LocalDatePeriod(
                    startDate = LocalDate.parse("2023-01-01"),
                    endDate = LocalDate.parse("2023-01-08"),
                ),
                LocalDatePeriod(
                    startDate = LocalDate.parse("2023-01-08"),
                    endDate = LocalDate.parse("2023-01-15"),
                ),
            )
        // # When
        val result = DatePeriodService.getPeriods(givenSelectableDuration, givenUsePeriodType, givenDatePeriod)
        // # Then
        assertEquals(expected, result)
    }
}