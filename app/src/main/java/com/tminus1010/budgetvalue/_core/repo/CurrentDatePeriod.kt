package com.tminus1010.budgetvalue._core.repo

import com.tminus1010.budgetvalue._shared.date_period_getter.DatePeriodGetter
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import javax.inject.Inject

class CurrentDatePeriod @Inject constructor(
    currentDate: CurrentDate,
    datePeriodGetter: DatePeriodGetter,
) {
    private val currentDatePeriod =
        currentDate()
            .map(datePeriodGetter::getDatePeriod)
            .distinctUntilChanged()
            .replayNonError(1)

    operator fun invoke() = currentDatePeriod
}