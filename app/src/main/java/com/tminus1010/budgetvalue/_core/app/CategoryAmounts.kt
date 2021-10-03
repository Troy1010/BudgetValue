package com.tminus1010.budgetvalue._core.app

import com.tminus1010.budgetvalue._core.all.extensions.copy
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal

data class CategoryAmounts constructor(private val map: Map<Category, BigDecimal> = emptyMap()) : Map<Category, BigDecimal> by map {
    constructor(vararg categoryAmounts: Pair<Category, BigDecimal>) : this(categoryAmounts.associate { it.first to it.second })

    init {
        if (CategoriesInteractor.defaultCategory in this.keys) error("CategoryAmounts should not have defaultCategory")
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
        return if (fillCategory == CategoriesInteractor.defaultCategory)
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

    /**
     * A [totalAmount] is how much the Transaction/Plan/whatever has in total.
     * The [categorizedAmount] is how much is categorized.
     * The difference is put into [defaultAmount]
     */
    fun defaultAmount(totalAmount: BigDecimal): BigDecimal {
        return totalAmount - categorizedAmount
    }

    val categorizedAmount by lazy {
        this.values.sum().toString().toMoneyBigDecimal()
    }
}