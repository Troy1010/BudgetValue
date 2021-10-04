package com.tminus1010.budgetvalue._core.app

import com.tminus1010.budgetvalue._core.data.repos.SettingsRepo
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import java.time.LocalDate
import java.time.Month
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatePeriodService @Inject constructor(
    settingsRepo: SettingsRepo,
) {
    private val anchorDay = LocalDate.of(2020, Month.JULY, 1)

    // TODO("This is pretty hacky..")
    private val blockSizeBS = settingsRepo.blockSize.toBehaviorSubject()
    private val anchorDateOffsetBS = settingsRepo.anchorDateOffset.toBehaviorSubject()

    fun isDatePeriodValid(datePeriod: LocalDatePeriod): Boolean =
        getDatePeriod(datePeriod.startDate, anchorDateOffsetBS.value!!, blockSizeBS.value!!) == datePeriod

    fun getDatePeriod(date: LocalDate): LocalDatePeriod =
        getDatePeriod(date, anchorDateOffsetBS.value!!, blockSizeBS.value!!)

    private fun getDatePeriod(date: LocalDate, anchorDateOffset: Long, blockSize: Long): LocalDatePeriod {
        val startDate = ChronoUnit.DAYS.between(anchorDay.plusDays(anchorDateOffset), date)
            .let { it % blockSize }
            .let { if (anchorDay.plusDays(anchorDateOffset).isAfter(date)) it else -it }
            .let { date.plusDays(it) }
        val endDate = startDate.plusDays(blockSize - 1)
        return LocalDatePeriod(startDate, endDate)
    }
}
