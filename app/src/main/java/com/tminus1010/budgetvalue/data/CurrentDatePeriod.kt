package com.tminus1010.budgetvalue.data

import com.tminus1010.budgetvalue.domain.DatePeriodService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class CurrentDatePeriod @Inject constructor(
    currentDate: CurrentDate,
    datePeriodService: DatePeriodService,
) {
    val currentDatePeriod =
        currentDate.flow
            .map(datePeriodService::getDatePeriod)
            .stateIn(GlobalScope, SharingStarted.Eagerly, datePeriodService.getDatePeriod(currentDate.flow.value))
}