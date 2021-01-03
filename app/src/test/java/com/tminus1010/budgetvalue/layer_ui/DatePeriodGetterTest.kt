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
    fun getDatePeriod() {
        // # Given
        val date1 = LocalDate.of(2020, Month.MARCH, 15)
        val date2 = LocalDate.of(2020, Month.MAY, 21)
        // # Stimulate & Verify
        assertTrue(date1 in datePeriodGetter.getDatePeriod(date1))
        assertEquals(
            LocalDatePeriod(
                LocalDate.of(2020, Month.MARCH, 12),
                LocalDate.of(2020, Month.MARCH, 25),
            ),
            datePeriodGetter.getDatePeriod(date1)
        )
        assertTrue(date2 in datePeriodGetter.getDatePeriod(date2))
        assertEquals(
            LocalDatePeriod(
                LocalDate.of(2020, Month.MAY, 10),
                LocalDate.of(2020, Month.MAY, 23),
            ),
            datePeriodGetter.getDatePeriod(date2)
        )
    }
}