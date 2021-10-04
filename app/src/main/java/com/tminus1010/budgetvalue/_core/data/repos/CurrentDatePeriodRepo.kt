package com.tminus1010.budgetvalue._core.data.repos

import com.tminus1010.budgetvalue._core.all.extensions.cold
import com.tminus1010.budgetvalue._shared.date_period_getter.DatePeriodService
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import javax.inject.Inject

class CurrentDatePeriodRepo @Inject constructor(
    currentDate: CurrentDate,
    datePeriodService: DatePeriodService,
) {
    val currentDatePeriod =
        currentDate()
            .map(datePeriodService::getDatePeriod)
            .distinctUntilChanged()
            .replayNonError(1)
            .cold()
}