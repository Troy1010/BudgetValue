package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.model_domain.Category
import java.math.BigDecimal

interface ICategoryAmountsConverter {
    fun toCategoryAmount(s: String?): Map<Category, BigDecimal>
    fun toString(categoryAmounts: Map<Category, BigDecimal>): String
}