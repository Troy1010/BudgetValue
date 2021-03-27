package com.tminus1010.budgetvalue._shared.domain

import com.tminus1010.budgetvalue._core.middleware.LocalDatePeriod
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate

interface IDatePeriodGetter {
    fun isDatePeriodValid(datePeriod: LocalDatePeriod): Boolean
    fun getDatePeriod(date: LocalDate): LocalDatePeriod
    fun getDatePeriodObservable(date: LocalDate): Observable<LocalDatePeriod>
    fun currentDatePeriod(): LocalDatePeriod
}