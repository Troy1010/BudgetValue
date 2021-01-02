package com.tminus1010.budgetvalue.model_app

import java.math.BigDecimal

data class HistoryColumnData(
    val title: String,
    val subTitle: String? = null,
    val defaultAmount: BigDecimal,
    val categoryAmounts: Map<Category, BigDecimal>,
)