package com.example.budgetvalue.model_app

import com.example.budgetvalue.SourceHashMap
import java.math.BigDecimal
import java.time.LocalDate

data class Reconciliation(
    val localDate: LocalDate,
    val categoryAmounts: SourceHashMap<Category, BigDecimal>
)