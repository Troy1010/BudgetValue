package com.tminus1010.budgetvalue.model_app

import com.tminus1010.budgetvalue.SourceHashMap
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

data class Plan(
    val localDatePeriod: Observable<LocalDatePeriod>,
    val planCategoryAmounts: SourceHashMap<Category, BigDecimal>
)