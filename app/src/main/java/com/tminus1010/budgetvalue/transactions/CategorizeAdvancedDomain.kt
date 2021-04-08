package com.tminus1010.budgetvalue.transactions

import com.tminus1010.budgetvalue.categories.models.Category
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategorizeAdvancedDomain @Inject constructor() {
    fun calcExactSplit(categories: Set<Category>, amount: BigDecimal): Map<Category, BigDecimal> {
        val x = -amount/categories.size.toBigDecimal()
        var xTotalSoFar = BigDecimal.ZERO
        return categories
            .associateWith {
                if (it != categories.last()) x.also { xTotalSoFar += it }
                else -amount - xTotalSoFar
            }
    }
}