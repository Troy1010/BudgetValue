package com.tminus1010.budgetvalue._core.data.repos

import com.tminus1010.budgetvalue._core.extensions.cold
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
            .cold()

    operator fun invoke() = currentDatePeriod
}