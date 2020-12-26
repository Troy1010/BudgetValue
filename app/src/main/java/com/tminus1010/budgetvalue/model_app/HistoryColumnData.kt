package com.tminus1010.budgetvalue.model_app

import java.math.BigDecimal
import java.time.LocalDate

data class HistoryColumnData(
    val categoryAmounts: Map<Category, BigDecimal>,
    val title: String,
    val subTitle: String? = null,
)