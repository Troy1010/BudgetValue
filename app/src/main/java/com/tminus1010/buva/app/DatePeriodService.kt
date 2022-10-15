package com.tminus1010.buva.app

import com.tminus1010.buva.data.SettingsRepo
import com.tminus1010.buva.domain.LocalDatePeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.Month
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatePeriodService @Inject constructor(
    private val settingsRepo: SettingsRepo,
) {
    @Deprecated("use getDatePeriod2, which emits on change")
    fun getDatePeriod(date: LocalDate): LocalDatePeriod =
        getDatePeriod(date, settingsRepo.anchorDateOffset.value, settingsRepo.blockSize.value)

    fun getDatePeriod2(date: LocalDate): Flow<LocalDatePeriod> =
        combine(settingsRepo.anchorDateOffset, settingsRepo.blockSize)
        { anchorDateOffset, blockSize ->
            getDatePeriod(date, anchorDateOffset, blockSize)
        }

    val getDatePeriodLambda =
        combine(settingsRepo.anchorDateOffset, settingsRepo.blockSize)
        { anchorDateOffset, blockSize ->
            { date: LocalDate -> getDatePeriod(date, anchorDateOffset, blockSize) }
        }

    companion object {
        private val anchorDay = LocalDate.of(2020, Month.JULY, 1)
        fun getDatePeriod(date: LocalDate, anchorDateOffset: Long, blockSize: Long): LocalDatePeriod {
            val startDate = ChronoUnit.DAYS.between(anchorDay.plusDays(anchorDateOffset), date)
                .let { it % blockSize }
                .let { if (anchorDay.plusDays(anchorDateOffset).isAfter(date)) it else -it }
                .let { date.plusDays(it) }
            val endDate = startDate.plusDays(blockSize - 1)
            return LocalDatePeriod(startDate, endDate)
        }
    }
}
