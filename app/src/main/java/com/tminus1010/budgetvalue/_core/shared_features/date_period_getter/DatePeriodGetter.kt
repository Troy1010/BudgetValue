package com.tminus1010.budgetvalue._core.shared_features.date_period_getter

import com.tminus1010.budgetvalue._core.middleware.LocalDatePeriod
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.shared_features.date_period_getter.data.ISettingsRepo
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate
import java.time.Month
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatePeriodGetter @Inject constructor(
    private val settingsRepo: ISettingsRepo
): IDatePeriodGetter {
    private val anchorDay = LocalDate.of(2020, Month.JULY, 1)
    override fun getDatePeriodObservable(date: LocalDate): Observable<LocalDatePeriod> =
        Rx.combineLatest(settingsRepo.anchorDateOffset, settingsRepo.blockSize)
            .map { (anchorDateOffset, blockSize) ->
                getDatePeriod(date, anchorDateOffset, blockSize)
            }

    override fun currentDatePeriod(): LocalDatePeriod =
        getDatePeriod(LocalDate.now())

    // TODO("This is pretty hacky..")
    private val blockSizeBS = settingsRepo.blockSize.toBehaviorSubject()
    private val anchorDateOffsetBS = settingsRepo.anchorDateOffset.toBehaviorSubject()

    override fun isDatePeriodValid(datePeriod: LocalDatePeriod): Boolean =
        getDatePeriod(datePeriod.startDate, anchorDateOffsetBS.value!!, blockSizeBS.value!!) == datePeriod
    override fun getDatePeriod(date: LocalDate): LocalDatePeriod =
        getDatePeriod(date, anchorDateOffsetBS.value!!, blockSizeBS.value!!)

    private fun getDatePeriod(date: LocalDate, anchorDateOffset:Long, blockSize:Long): LocalDatePeriod {
        val startDate = ChronoUnit.DAYS.between(anchorDay.plusDays(anchorDateOffset), date)
            .let { it % blockSize }
            .let { if (anchorDay.plusDays(anchorDateOffset).isAfter(date)) it else -it }
            .let { date.plusDays(it) }
        val endDate = startDate.plusDays(blockSize - 1)
        return LocalDatePeriod(startDate, endDate)
    }
}
