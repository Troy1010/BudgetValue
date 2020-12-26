package com.tminus1010.budgetvalue.model_app

import com.tminus1010.budgetvalue.SourceHashMap
import java.math.BigDecimal
import java.time.LocalDate

data class Reconciliation(
    val localDate: LocalDate,
    val categoryAmounts: SourceHashMap<Category, BigDecimal>
)