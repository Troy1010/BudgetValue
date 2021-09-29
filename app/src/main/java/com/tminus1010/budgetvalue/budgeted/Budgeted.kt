package com.tminus1010.budgetvalue.budgeted

import com.tminus1010.budgetvalue.categories.models.Category
import java.math.BigDecimal

class Budgeted(
    val categoryAmounts: Map<Category, BigDecimal>,
    val defaultAmount: BigDecimal,
)