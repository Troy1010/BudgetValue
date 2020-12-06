package com.example.budgetvalue.model_app

import com.example.budgetvalue.SourceHashMap
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

data class Plan(
    val localDatePeriod: Observable<LocalDatePeriod>,
    val planCategoryAmounts: SourceHashMap<Category, BigDecimal>
)