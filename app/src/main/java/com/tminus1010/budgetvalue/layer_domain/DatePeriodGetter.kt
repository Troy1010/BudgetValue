package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.combineLatestAsTuple
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.model_app.LocalDatePeriod
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate
import java.time.Month
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatePeriodGetter @Inject constructor(
    repo: Repo
): IDatePeriodGetter {
    private val blockSize = repo.fetchBlockSize()
    private val anchorDateOffset = repo.fetchAnchorDateOffset()
    private val anchorDay = LocalDate.of(2020, Month.JULY, 1)
    override fun getDatePeriodObservable(date: LocalDate): Observable<LocalDatePeriod> {
        return combineLatestAsTuple(anchorDateOffset, blockSize)
            .map { (anchorDateOffset, blockSize) ->
                getDatePeriod(date, anchorDateOffset, blockSize)
            }
    }
    // TODO("This is pretty hacky..")
    private val blockSizeBS = blockSize.toBehaviorSubject()
    private val anchorDateOffsetBS = anchorDateOffset.toBehaviorSubject()

    override fun isDatePeriodValid(datePeriod: LocalDatePeriod): Boolean {
        return getDatePeriod(datePeriod.startDate, anchorDateOffsetBS.value!!, blockSizeBS.value!!) == datePeriod
    }
    override fun getDatePeriod(date: LocalDate): LocalDatePeriod {
        return getDatePeriod(date, anchorDateOffsetBS.value!!, blockSizeBS.value!!)
    }

    private fun getDatePeriod(date: LocalDate, anchorDateOffset:Long, blockSize:Long): LocalDatePeriod {
        val startDate = ChronoUnit.DAYS.between(anchorDay.plusDays(anchorDateOffset), date)
            .let { it % blockSize }
            .let { if (anchorDay.plusDays(anchorDateOffset).isAfter(date)) it else -it }
            .let { date.plusDays(it) }
        val endDate = startDate.plusDays(blockSize - 1)
        return LocalDatePeriod(startDate, endDate)
    }
}
