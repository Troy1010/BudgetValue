package com.tminus1010.budgetvalue._core.models

import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal

class CategoryAmountFormulas(map: Map<Category, AmountFormula> = emptyMap()) : HashMap<Category, AmountFormula>(map) {
    fun Map<Category, AmountFormula>.calcFillAmountFormula(fillCategory: Category, amount: BigDecimal): AmountFormula {
        return AmountFormula(
            (amount - this.filter { it.key != fillCategory }.map { it.value.calcAmount(amount) }.sum()),
            BigDecimal.ZERO,
        )
    }

    override fun put(key: Category, value: AmountFormula): AmountFormula? {
        return if (value.isZero())
            remove(key)
        else
            super.put(key, value)
    }

    override fun putAll(from: Map<out Category, AmountFormula>) {
        super.putAll(from.filter { !it.value.isZero() })
    }

    operator fun plus(map: Map<Category, AmountFormula>): CategoryAmountFormulas {
        return this.apply { putAll(map) }
    }
}