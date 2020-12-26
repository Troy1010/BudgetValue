package com.tminus1010.budgetvalue.layer_ui

import com.tminus1010.budgetvalue.combineLatestAsTuple
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.model_app.LocalDatePeriod
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate
import java.time.Month
import java.time.Period
import javax.inject.Inject

class DatePeriodGetter @Inject constructor(repo: Repo) {
    val blockSize = repo.fetchBlockSize()
    val anchorDateOffset = repo.fetchAnchorDateOffset()
    val anchorDay = LocalDate.of(2020, Month.JULY, 1)
    fun getDatePeriod(date: LocalDate): Observable<LocalDatePeriod> {
        return combineLatestAsTuple(anchorDateOffset, blockSize)
            .map { (anchorDateOffset, blockSize) ->
                var offset =
                    Period.between(anchorDay.plusDays(anchorDateOffset), date).days % blockSize
                offset *= if (anchorDay.isAfter(date)) 1 else -1
                val startDate = date.plusDays(offset)
                val endDate = startDate.plusDays(blockSize - 1)
                LocalDatePeriod(startDate, endDate)
            }
    }
}
