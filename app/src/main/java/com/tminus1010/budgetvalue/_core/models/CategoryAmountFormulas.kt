package com.tminus1010.budgetvalue._core.models

import com.tminus1010.budgetvalue._core.extensions.copy
import com.tminus1010.budgetvalue._core.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal

data class CategoryAmountFormulas constructor(private val map: Map<Category, AmountFormula> = emptyMap()) : Map<Category, AmountFormula> by map {
    constructor(vararg categoryAmountFormulas: Pair<Category, AmountFormula>) : this(categoryAmountFormulas.associate { it.first to it.second })

    init {
        if (CategoriesDomain.defaultCategory in this.keys) error("CategoryAmountFormulas should not have defaultCategory")
    }

    operator fun plus(map: Map<Category, AmountFormula>): CategoryAmountFormulas {
        return CategoryAmountFormulas(map + this)
    }

    fun fillIntoCategory(fillCategory: Category, totalAmount: BigDecimal): CategoryAmountFormulas {
        return if (fillCategory == CategoriesDomain.defaultCategory)
            this
        else
            this
                .filter { it.key != fillCategory }
                .copy(fillCategory to AmountFormula.Value(calcFillAmount(fillCategory, totalAmount)))
                .let { CategoryAmountFormulas(it) }
    }

    fun calcFillAmount(fillCategory: Category, totalAmount: BigDecimal): BigDecimal {
        return totalAmount - this.filter { it.key != fillCategory }.map { it.value.calcAmount(totalAmount) }.sum()
    }

    fun defaultAmount(totalAmount: BigDecimal): BigDecimal {
        return totalAmount - this.map { it.value.calcAmount(totalAmount) }.sum().toString().toMoneyBigDecimal()
    }
}