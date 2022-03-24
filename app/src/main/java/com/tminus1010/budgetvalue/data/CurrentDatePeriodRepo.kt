package com.tminus1010.budgetvalue.data

import com.tminus1010.budgetvalue.all_layers.extensions.cold
import com.tminus1010.budgetvalue.domain.DatePeriodService
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import javax.inject.Inject

class CurrentDatePeriodRepo @Inject constructor(
    currentDateRepo: CurrentDateRepo,
    datePeriodService: DatePeriodService,
) {
    val currentDatePeriod =
        currentDateRepo()
            .map(datePeriodService::getDatePeriod)
            .distinctUntilChanged()
            .replayNonError(1)
            .cold()
}