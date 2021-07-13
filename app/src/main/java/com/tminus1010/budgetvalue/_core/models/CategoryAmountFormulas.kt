package com.tminus1010.budgetvalue._core.models

import com.tminus1010.budgetvalue._core.extensions.copy
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal

data class CategoryAmountFormulas constructor(val map: Map<Category, AmountFormula> = emptyMap()) : Map<Category, AmountFormula> by map {

    fun Map<Category, AmountFormula>.calcFillAmountFormula(fillCategory: Category, amount: BigDecimal): AmountFormula {
        return AmountFormula.Value(amount - this.filter { it.key != fillCategory }.map { it.value.calcAmount(amount) }.sum())
    }

    operator fun plus(map: Map<Category, AmountFormula>): CategoryAmountFormulas {
        return CategoryAmountFormulas(map + this)
    }

    fun fillIntoCategory(category: Category, amount: BigDecimal): CategoryAmountFormulas {
        return if (category == CategoriesDomain.defaultCategory)
            this
        else
            this
                .filter { it.key != category }
                .let { it.copy(category to it.calcFillAmountFormula(category, amount)) }
                .let { CategoryAmountFormulas(it) }
    }
}