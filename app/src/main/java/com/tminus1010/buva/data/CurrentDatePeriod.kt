package com.tminus1010.buva.data

import com.tminus1010.buva.app.DatePeriodService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class CurrentDatePeriod @Inject constructor(
    currentDate: CurrentDate,
    datePeriodService: DatePeriodService,
) {
    val flow =
        currentDate.flow
            .flatMapConcat { datePeriodService.getDatePeriod2(it) }
            .stateIn(GlobalScope, SharingStarted.Eagerly, runBlocking { datePeriodService.getDatePeriod2(currentDate.flow.value).first() })
}