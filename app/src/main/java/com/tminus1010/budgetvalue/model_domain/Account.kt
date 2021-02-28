package com.tminus1010.budgetvalue.model_domain

import java.math.BigDecimal


data class Account(
    var name: String,
    var amount: BigDecimal,
    val id: Int = 0,
)