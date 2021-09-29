package com.tminus1010.budgetvalue.history.models

import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal

interface IHistoryColumnData {
    val defaultAmount: BigDecimal
    val categoryAmounts: Map<Category, BigDecimal>
    fun totalAmount(): BigDecimal {
        return categoryAmounts.values.sum() + defaultAmount
    }
}