package com.tminus1010.budgetvalue.layer_ui

import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.model_app.LocalDatePeriod
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Observable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.Month

class DatePeriodGetterTest {
    val repo = mockk<Repo>()
        .also { every { it.fetchAnchorDateOffset() } returns Observable.just(0) }
        .also { every { it.fetchBlockSize() } returns Observable.just(14) }
    val datePeriodGetter = DatePeriodGetter(repo)

    @Test
    fun getDatePeriod_GivenValuesNearAnchorDate() {
        // # Given
        val date1 = LocalDate.of(2020, Month.JULY, 6)
        val date2 = LocalDate.of(2020, Month.JULY, 15)
        val date3 = LocalDate.of(2020, Month.JULY, 27)
        val date4 = LocalDate.of(2020, Month.AUGUST, 5)
        // # Stimulate & Verify
        assertEquals(
            LocalDatePeriod(
                LocalDate.of(2020, Month.JULY, 1),
                LocalDate.of(2020, Month.JULY, 14),
            ),
            datePeriodGetter.getDatePeriod(date1)
        )
        assertEquals(
            LocalDatePeriod(
                LocalDate.of(2020, Month.JULY, 15),
                LocalDate.of(2020, Month.JULY, 28),
            ),
            datePeriodGetter.getDatePeriod(date2)
        )
        assertEquals(
            LocalDatePeriod(
                LocalDate.of(2020, Month.JULY, 15),
                LocalDate.of(2020, Month.JULY, 28),
            ),
            datePeriodGetter.getDatePeriod(date3)
        )
        assertEquals(
            LocalDatePeriod(
                LocalDate.of(2020, Month.JULY, 29),
                LocalDate.of(2020, Month.AUGUST, 11),
            ),
            datePeriodGetter.getDatePeriod(date4)
        )
    }
}