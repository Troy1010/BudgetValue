package com.tminus1010.budgetvalue.categories

import com.tminus1010.budgetvalue.categories.models.Category
import java.math.BigDecimal

interface ICategoryAmountsConverter {
    fun toCategoryAmounts(s: String?): Map<Category, BigDecimal>
    fun toJson(categoryAmounts: Map<Category, BigDecimal>): String
}