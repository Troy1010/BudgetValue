package com.tminus1010.budgetvalue._core.domain

import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal

data class CategoryAmountFormulas constructor(private val map: Map<Category, AmountFormula> = emptyMap()) : Map<Category, AmountFormula> by map {
    constructor(vararg categoryAmountFormulas: Pair<Category, AmountFormula>) : this(categoryAmountFormulas.associate { it.first to it.second })

    init {
        if (Category.DEFAULT in this.keys) error("CategoryAmountFormulas should not have defaultCategory")
    }

    operator fun plus(map: Map<Category, AmountFormula>): CategoryAmountFormulas {
        return CategoryAmountFormulas(this.toMutableMap().apply { putAll(map) })
    }

    fun fillIntoCategory(fillCategory: Category, totalAmount: BigDecimal): CategoryAmountFormulas {
        return if (fillCategory == Category.DEFAULT)
            this
        else
            this
                .run { if (fillCategory in this.keys) this else this.plus(fillCategory to AmountFormula.Value.ZERO) }
                .mapValues { getAmountFormula(it.key, it.value, fillCategory, totalAmount) }
                .let { CategoryAmountFormulas(it) }
    }

    fun getAmountFormula(category: Category, amountFormula: AmountFormula, fillCategory: Category, totalAmount: BigDecimal): AmountFormula {
        return when {
            category == fillCategory -> AmountFormula.Value(calcFillAmount(fillCategory, totalAmount))
            else -> amountFormula
        }
    }

    fun calcFillAmount(fillCategory: Category, totalAmount: BigDecimal): BigDecimal {
        return totalAmount - this.filter { it.key != fillCategory }.map { it.value.calcAmount(totalAmount) }.sum()
    }

    fun defaultAmount(totalAmount: BigDecimal): BigDecimal {
        return totalAmount - this.map { it.value.calcAmount(totalAmount) }.sum().toString().toMoneyBigDecimal()
    }
}