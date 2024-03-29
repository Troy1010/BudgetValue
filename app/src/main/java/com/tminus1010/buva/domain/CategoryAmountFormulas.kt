package com.tminus1010.buva.domain

import android.os.Parcelable
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class CategoryAmountFormulas constructor(private val map: Map<Category, AmountFormula> = emptyMap()) : Map<Category, AmountFormula> by map, Parcelable {
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
                .run { if (fillCategory in this.keys) this else this.plus(fillCategory to AmountFormula.Value(BigDecimal.ZERO)) }
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

    fun toCategoryAmounts(totalAmount: BigDecimal): CategoryAmounts {
        return CategoryAmounts(this.mapValues { it.value.calcAmount(totalAmount) })
    }
}