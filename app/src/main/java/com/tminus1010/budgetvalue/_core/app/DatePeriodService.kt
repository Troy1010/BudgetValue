package com.tminus1010.budgetvalue._core.app

import com.tminus1010.budgetvalue._core.data.repos.SettingsRepo
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import java.time.LocalDate
import java.time.Month
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatePeriodService @Inject constructor(
    private val settingsRepo: SettingsRepo,
) {
    private val anchorDay = LocalDate.of(2020, Month.JULY, 1)

    fun isDatePeriodValid(datePeriod: LocalDatePeriod): Boolean =
        getDatePeriod(datePeriod.startDate, settingsRepo.anchorDateOffset.value!!, settingsRepo.blockSize.value!!) == datePeriod

    fun getDatePeriod(date: LocalDate): LocalDatePeriod =
        getDatePeriod(date, settingsRepo.anchorDateOffset.value!!, settingsRepo.blockSize.value!!)

    private fun getDatePeriod(date: LocalDate, anchorDateOffset: Long, blockSize: Long): LocalDatePeriod {
        val startDate = ChronoUnit.DAYS.between(anchorDay.plusDays(anchorDateOffset), date)
            .let { it % blockSize }
            .let { if (anchorDay.plusDays(anchorDateOffset).isAfter(date)) it else -it }
            .let { date.plusDays(it) }
        val endDate = startDate.plusDays(blockSize - 1)
        return LocalDatePeriod(startDate, endDate)
    }
}
