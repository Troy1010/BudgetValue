package com.tminus1010.budgetvalue.model_app

import com.tminus1010.budgetvalue.model_data.Category
import java.math.BigDecimal

interface IAmountAndCA {
    val amount: BigDecimal
    val categoryAmounts: Map<Category, BigDecimal>
    val defaultAmount: BigDecimal
}