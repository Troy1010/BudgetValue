package com.example.budgetvalue.model_app

import java.math.BigDecimal

data class Block(
    val localDatePeriod: LocalDatePeriod,
    val amount: BigDecimal,
    val categoryAmounts: Map<Category, BigDecimal>
)