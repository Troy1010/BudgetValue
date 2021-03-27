package com.tminus1010.budgetvalue.categories

import java.math.BigDecimal

interface ICategoryAmountsConverter {
    fun toCategoryAmount(s: String?): Map<Category, BigDecimal>
    fun toString(categoryAmounts: Map<Category, BigDecimal>): String
}