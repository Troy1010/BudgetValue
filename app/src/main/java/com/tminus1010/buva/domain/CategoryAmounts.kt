package com.tminus1010.buva.domain

import android.os.Parcelable
import com.tminus1010.buva.all_layers.extensions.copy
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.math.BigDecimal

@Suppress("PROPERTY_WONT_BE_SERIALIZED")
@Parcelize
data class CategoryAmounts constructor(private val map: @RawValue Map<Category, BigDecimal> = emptyMap()) : Map<Category, BigDecimal> by map, Parcelable {
    constructor(vararg categoryAmounts: Pair<Category, BigDecimal>) : this(categoryAmounts.associate { it.first to it.second })

    init {
        if (Category.DEFAULT in this.keys) error("CategoryAmounts should not have defaultCategory. WARNING: If a sortedMap was given, remember that equality is determined by the sortedBy lambda.")
    }

    operator fun plus(map: Map<Category, BigDecimal>): CategoryAmounts {
        return CategoryAmounts(this.toMutableMap().apply { putAll(map) })
    }

    /**
     * This could use a better name.. but it essentially lets you define a new map from 2 maps.
     */
    fun squashTogether(other: Map<Category, BigDecimal>, lambda: (BigDecimal?, BigDecimal) -> BigDecimal): CategoryAmounts {
        return Companion.squashTogether(listOf(this, other), lambda)
    }

    fun addTogether(other: Map<Category, BigDecimal>): CategoryAmounts {
        return squashTogether(other) { a, b -> (a ?: BigDecimal.ZERO) + b }
    }

    fun maxTogether(other: Map<Category, BigDecimal>): CategoryAmounts {
        return squashTogether(other) { a, b -> maxOf(a ?: BigDecimal.ZERO, b) }
    }

    fun subtractTogether(other: Map<Category, BigDecimal>): CategoryAmounts {
        return addTogether(other.mapValues { -it.value })
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

    fun fillToGetTargetDefaultAmount(category: Category, targetDefaultAmount: BigDecimal): CategoryAmounts {
        return if (category == Category.DEFAULT)
            this
        else
            this
                .filter { it.key != category }
                .copy(category to calcCategoryAmountToGetTargetDefaultAmount(category, targetDefaultAmount))
                .let { CategoryAmounts(it) }
    }

    fun calcFillAmount(fillCategory: Category, total: BigDecimal): BigDecimal {
        return total - this.filter { it.key != fillCategory }.values.sum()
    }

    fun calcCategoryAmountToGetTargetDefaultAmount(category: Category, targetDefaultAmount: BigDecimal): BigDecimal {
        return this.toMutableMap().also { it[category] = BigDecimal.ZERO }
            .let { CategoryAmounts(it) }
            .let { -targetDefaultAmount - it.categorizedAmount }
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

    companion object {
        fun addTogether(vararg categoryAmounts: Map<Category, BigDecimal>): CategoryAmounts {
            return squashTogether(categoryAmounts.asIterable()) { a, b -> (a ?: BigDecimal.ZERO) + b }
        }

        fun squashTogether(vararg categoryAmounts: Map<Category, BigDecimal>, lambda: (BigDecimal?, BigDecimal) -> BigDecimal): CategoryAmounts {
            return squashTogether(categoryAmounts.asIterable(), lambda)
        }

        fun squashTogether(categoryAmounts: Iterable<Map<Category, BigDecimal>>, lambda: (BigDecimal?, BigDecimal) -> BigDecimal): CategoryAmounts {
            return categoryAmounts
                .fold(hashMapOf<Category, BigDecimal>()) { acc, map ->
                    map.forEach { (k, v) -> acc[k] = lambda(acc[k], v) }
                    acc
                }
                .let { CategoryAmounts(it) }
        }
    }
}