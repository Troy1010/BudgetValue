package com.tminus1010.budgetvalue.all_features.domain

import com.tminus1010.budgetvalue.all_features.all_layers.extensions.copy
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal

data class CategoryAmounts constructor(private val map: Map<Category, BigDecimal> = emptyMap()) : Map<Category, BigDecimal> by map {
    constructor(vararg categoryAmounts: Pair<Category, BigDecimal>) : this(categoryAmounts.associate { it.first to it.second })

    init {
        if (Category.DEFAULT in this.keys) error("CategoryAmounts should not have defaultCategory. WARNING: If a sortedMap was given, remember that equality is determined by the sortedBy lambda.")
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

    fun replaceKey(originalCategory: Category, newCategory: Category): CategoryAmounts {
        val categoryAmounts = this.toMutableMap()
        categoryAmounts[originalCategory]?.also { categoryAmounts[newCategory] = it }
        categoryAmounts.remove(originalCategory)
        return CategoryAmounts(categoryAmounts)
    }

    fun fillIntoCategory(fillCategory: Category, totalAmount: BigDecimal): CategoryAmounts {
        return if (fillCategory == Category.DEFAULT)
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
     * A [total] is how much the Transaction/Plan/whatever has in total.
     * The [categorizedAmount] is how much is categorized.
     * The difference is put into [defaultAmount]
     */
    fun defaultAmount(total: BigDecimal): BigDecimal {
        return total - categorizedAmount
    }

    /**
     * A [total] is how much the Transaction/Plan/whatever has in total.
     * The [categorizedAmount] is how much is categorized.
     * The difference is put into [defaultAmount]
     */
    fun total(defaultAmount: BigDecimal): BigDecimal {
        return categorizedAmount + defaultAmount
    }

    val categorizedAmount by lazy {
        this.values.sum().toString().toMoneyBigDecimal()
    }
}