package com.tminus1010.budgetvalue.model_app

import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

data class Plan(
    val localDatePeriod: Observable<LocalDatePeriod>,
    val defaultAmount: BigDecimal,
    val categoryAmounts: Map<Category, BigDecimal>,
)