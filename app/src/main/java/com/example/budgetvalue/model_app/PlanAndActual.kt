package com.example.budgetvalue.model_app

import com.example.budgetvalue.SourceHashMap
import java.math.BigDecimal

data class PlanAndActual(
    val localDatePeriod: LocalDatePeriod,
    val planCategoryAmounts: SourceHashMap<Category, BigDecimal>,
    val actualCategoryAmounts: SourceHashMap<Category, BigDecimal>
)