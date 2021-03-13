package com.tminus1010.budgetvalue.model_domain

import java.math.BigDecimal

interface IAmountAndCA {
    val amount: BigDecimal
    val categoryAmounts: Map<Category, BigDecimal>
    val defaultAmount: BigDecimal
}