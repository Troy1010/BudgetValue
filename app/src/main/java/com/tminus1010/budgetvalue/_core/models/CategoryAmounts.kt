package com.tminus1010.budgetvalue._core.models

import com.tminus1010.budgetvalue._core.extensions.copy
import com.tminus1010.budgetvalue._core.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal

data class CategoryAmounts constructor(private val map: Map<Category, BigDecimal> = emptyMap()) : Map<Category, BigDecimal> by map {
    constructor(vararg categoryAmounts: Pair<Category, BigDecimal>) : this(categoryAmounts.associate { it.first to it.second })

    init {
        if (CategoriesDomain.defaultCategory in this.keys) error("CategoryAmounts should not have defaultCategory")
    }

    operator fun plus(map: Map<Category, BigDecimal>): CategoryAmounts {
        return CategoryAmounts(this.toMutableMap().apply { putAll(map) })
    }

    fun addTogether(other: Map<Category, BigDecimal>): CategoryAmounts {
        return listOf(this, other)
            .fold(hashMapOf<Category, BigDecimal>()) { acc, map ->
                map.forEach { (k, v) -> acc[k] = (acc[k] ?: BigDecimal.ZERO) + v }
                acc
            }
            .let { CategoryAmounts(it) }
    }

    fun fillIntoCategory(fillCategory: Category, totalAmount: BigDecimal): CategoryAmounts {
        return if (fillCategory == CategoriesDomain.defaultCategory)
            this
        else
            this
                .filter { it.key != fillCategory }
                .copy(fillCategory to calcFillAmount(fillCategory, totalAmount))
                .let { CategoryAmounts(it) }
    }

    fun calcFillAmount(fillCategory: Category, totalAmount: BigDecimal): BigDecimal {
        return totalAmount - this.filter { it.key != fillCategory }.values.sum()
    }

    fun defaultAmount(totalAmount: BigDecimal): BigDecimal {
        return totalAmount - this.values.sum().toString().toMoneyBigDecimal()
    }
}