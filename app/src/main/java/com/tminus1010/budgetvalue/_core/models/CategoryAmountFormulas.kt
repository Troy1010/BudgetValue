package com.tminus1010.budgetvalue._core.models

import com.tminus1010.budgetvalue._core.extensions.copy
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal

data class CategoryAmountFormulas constructor(val map: Map<Category, AmountFormula> = emptyMap()) : Map<Category, AmountFormula> by map {
    operator fun plus(map: Map<Category, AmountFormula>): CategoryAmountFormulas {
        return CategoryAmountFormulas(map + this)
    }

    fun fillIntoCategory(fillCategory: Category, amount: BigDecimal): CategoryAmountFormulas {
        return if (fillCategory == CategoriesDomain.defaultCategory)
            this
        else
            this
                .filter { it.key != fillCategory }
                .copy(fillCategory to AmountFormula.Value(calcFillAmount(fillCategory, amount)))
                .let { CategoryAmountFormulas(it) }
    }

    fun calcFillAmount(fillCategory: Category, amount: BigDecimal): BigDecimal {
        return amount - this.filter { it.key != fillCategory }.map { it.value.calcAmount(amount) }.sum()
    }
}