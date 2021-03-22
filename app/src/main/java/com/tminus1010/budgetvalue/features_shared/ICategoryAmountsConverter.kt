package com.tminus1010.budgetvalue.features_shared

import com.tminus1010.budgetvalue.features.categories.Category
import java.math.BigDecimal

interface ICategoryAmountsConverter {
    fun toCategoryAmount(s: String?): Map<Category, BigDecimal>
    fun toString(categoryAmounts: Map<Category, BigDecimal>): String
}