package com.example.budgetvalue.models

import java.math.BigDecimal

data class Transaction(
    var date: String?,
    var description: String,
    var amount: String,
    val id: Int = 0
)