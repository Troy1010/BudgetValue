package com.tminus1010.budgetvalue._core.data.repos

import com.tminus1010.budgetvalue._core.all.extensions.cold
import com.tminus1010.budgetvalue._shared.date_period_getter.DatePeriodGetter
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import javax.inject.Inject

class CurrentDatePeriodRepo @Inject constructor(
    currentDate: CurrentDate,
    datePeriodGetter: DatePeriodGetter,
) {
    val currentDatePeriod =
        currentDate()
            .map(datePeriodGetter::getDatePeriod)
            .distinctUntilChanged()
            .replayNonError(1)
            .cold()
}