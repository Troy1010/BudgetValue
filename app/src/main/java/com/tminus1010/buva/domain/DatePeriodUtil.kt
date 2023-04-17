package com.tminus1010.buva.domain

import com.tminus1010.buva.ui.review.review_pie_chart.SelectableDuration
import com.tminus1010.buva.ui.review.review_pie_chart.UsePeriodType
import com.tminus1010.tmcommonkotlin.core.extensions.nextOrSame
import com.tminus1010.tmcommonkotlin.core.extensions.previousOrSame
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

object DatePeriodUtil {
    fun getPeriods(selectedDuration: SelectableDuration, usePeriodType: UsePeriodType, entirePeriod: LocalDatePeriod): List<LocalDatePeriod> {
        return sequence {
            var currentPageNumber = 0L
            while (true) {
                val x = getPeriod(selectedDuration, usePeriodType, entirePeriod.endDate, currentPageNumber) ?: break
                if (x.endDate <= entirePeriod.startDate) break
                yield(x)
                currentPageNumber += 1
            }
        }.toList().reversed()
    }

    fun getPeriod(userSelectedDuration: SelectableDuration, userUsePeriodType: UsePeriodType, endDate: LocalDate, currentPageNumber: Long): LocalDatePeriod? {
        return when (userSelectedDuration) {
            SelectableDuration.BY_WEEK ->
                when (userUsePeriodType) {
                    UsePeriodType.USE_DAY_COUNT_PERIODS ->
                        LocalDatePeriod(
                            endDate.minusDays((currentPageNumber + 1) * 7),
                            endDate.minusDays((currentPageNumber) * 7),
                        )
                    UsePeriodType.USE_CALENDAR_PERIODS -> {
                        val dateToConsider = endDate.minusWeeks(currentPageNumber)
                        LocalDatePeriod(
                            dateToConsider.previousOrSame(DayOfWeek.SUNDAY),
                            dateToConsider.nextOrSame(DayOfWeek.SATURDAY),
                        )
                    }
                }
            SelectableDuration.BY_MONTH ->
                when (userUsePeriodType) {
                    UsePeriodType.USE_DAY_COUNT_PERIODS ->
                        LocalDatePeriod(
                            endDate.minusDays((currentPageNumber + 1) * 30),
                            endDate.minusDays((currentPageNumber) * 30),
                        )
                    UsePeriodType.USE_CALENDAR_PERIODS -> {
                        val dateToConsider = endDate.minusMonths(currentPageNumber)
                        LocalDatePeriod(
                            dateToConsider.withDayOfMonth(1),
                            dateToConsider.withDayOfMonth(dateToConsider.lengthOfMonth()),
                        )
                    }
                }
            SelectableDuration.BY_3_MONTHS ->
                when (userUsePeriodType) {
                    UsePeriodType.USE_DAY_COUNT_PERIODS ->
                        LocalDatePeriod(
                            endDate.minusDays((currentPageNumber + 1) * 365 / 4),
                            endDate.minusDays((currentPageNumber) * 365 / 4),
                        )
                    UsePeriodType.USE_CALENDAR_PERIODS -> {
                        val dateToConsider = endDate.minusMonths(currentPageNumber * 3)
                        val firstQuarter =
                            LocalDatePeriod(
                                LocalDate.of(dateToConsider.year, Month.JANUARY, 1),
                                LocalDate.of(dateToConsider.year, Month.MARCH, Month.MARCH.length(dateToConsider.isLeapYear)),
                            )
                        val secondQuarter =
                            LocalDatePeriod(
                                LocalDate.of(dateToConsider.year, Month.APRIL, 1),
                                LocalDate.of(dateToConsider.year, Month.JUNE, Month.JUNE.length(dateToConsider.isLeapYear)),
                            )
                        val thirdQuarter =
                            LocalDatePeriod(
                                LocalDate.of(dateToConsider.year, Month.JULY, 1),
                                LocalDate.of(dateToConsider.year, Month.SEPTEMBER, Month.SEPTEMBER.length(dateToConsider.isLeapYear)),
                            )
                        val fourthQuarter =
                            LocalDatePeriod(
                                LocalDate.of(dateToConsider.year, Month.OCTOBER, 1),
                                LocalDate.of(dateToConsider.year, Month.DECEMBER, Month.DECEMBER.length(dateToConsider.isLeapYear)),
                            )
                        when (dateToConsider) {
                            in firstQuarter -> firstQuarter
                            in secondQuarter -> secondQuarter
                            in thirdQuarter -> thirdQuarter
                            in fourthQuarter -> fourthQuarter
                            else -> error("dateToConsider:${dateToConsider} was somehow not in any quarters.")
                        }
                    }
                }
            SelectableDuration.BY_6_MONTHS ->
                when (userUsePeriodType) {
                    UsePeriodType.USE_DAY_COUNT_PERIODS ->
                        LocalDatePeriod(
                            endDate.minusDays((currentPageNumber + 1) * 365 / 2),
                            endDate.minusDays((currentPageNumber) * 365 / 2),
                        )
                    UsePeriodType.USE_CALENDAR_PERIODS -> {
                        val dateToConsider = endDate.minusMonths(currentPageNumber * 6)
                        val firstHalfOfYear =
                            LocalDatePeriod(
                                LocalDate.of(dateToConsider.year, Month.JANUARY, 1),
                                LocalDate.of(dateToConsider.year, Month.JUNE, Month.JUNE.length(dateToConsider.isLeapYear)),
                            )
                        val secondHalfOfYear =
                            LocalDatePeriod(
                                LocalDate.of(dateToConsider.year, Month.JULY, 1),
                                LocalDate.of(dateToConsider.year, Month.DECEMBER, Month.DECEMBER.length(dateToConsider.isLeapYear)),
                            )
                        if (dateToConsider in firstHalfOfYear) firstHalfOfYear else secondHalfOfYear
                    }
                }
            SelectableDuration.BY_YEAR ->
                when (userUsePeriodType) {
                    UsePeriodType.USE_DAY_COUNT_PERIODS ->
                        LocalDatePeriod(
                            endDate.minusDays((currentPageNumber + 1) * 365),
                            endDate.minusDays((currentPageNumber) * 365),
                        )
                    UsePeriodType.USE_CALENDAR_PERIODS -> {
                        val dateToConsider = endDate.minusYears(currentPageNumber)
                        LocalDatePeriod(
                            LocalDate.of(dateToConsider.year, Month.JANUARY, 1),
                            LocalDate.of(dateToConsider.year, Month.DECEMBER, Month.DECEMBER.length(dateToConsider.isLeapYear)),
                        )
                    }
                }
            SelectableDuration.FOREVER ->
                null
        }
    }
}