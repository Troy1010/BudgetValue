package com.tminus1010.budgetvalue.model_app

import java.math.BigDecimal

interface IAmountAndCA {
    val amount: BigDecimal
    val categoryAmounts: Map<Category, BigDecimal>
    val defaultAmount: BigDecimal
}