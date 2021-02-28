package com.tminus1010.budgetvalue.layer_ui

import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.layer_domain.DatePeriodGetter
import com.tminus1010.budgetvalue.model_domain.LocalDatePeriod
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Observable
import org.junit.Assert.assertEquals
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
            LocalDatePeriod(LocalDate.parse("2020-07-01"), LocalDate.parse("2020-07-14")),
            datePeriodGetter.getDatePeriod(date1)
        )
        assertEquals(
            LocalDatePeriod(LocalDate.parse("2020-07-15"), LocalDate.parse("2020-07-28")),
            datePeriodGetter.getDatePeriod(date2)
        )
        assertEquals(
            LocalDatePeriod(LocalDate.parse("2020-07-15"), LocalDate.parse("2020-07-28")),
            datePeriodGetter.getDatePeriod(date3)
        )
        assertEquals(
            LocalDatePeriod(LocalDate.parse("2020-07-29"), LocalDate.parse("2020-08-11")),
            datePeriodGetter.getDatePeriod(date4)
        )
    }
}