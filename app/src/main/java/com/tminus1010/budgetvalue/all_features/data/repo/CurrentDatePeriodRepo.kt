package com.tminus1010.budgetvalue.all_features.data.repo

import com.tminus1010.budgetvalue.all_features.all_layers.extensions.cold
import com.tminus1010.budgetvalue.all_features.domain.DatePeriodService
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