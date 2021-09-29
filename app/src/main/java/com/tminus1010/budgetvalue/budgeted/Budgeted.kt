package com.tminus1010.budgetvalue.budgeted

import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.history.models.IHistoryColumnData
import java.math.BigDecimal

class Budgeted(
    override val categoryAmounts: Map<Category, BigDecimal>,
    override val defaultAmount: BigDecimal,
) : IHistoryColumnData