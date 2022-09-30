package com.tminus1010.buva.domain

import androidx.room.Ignore
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal

sealed class CategoryAmountsAndTotal {
    abstract val categoryAmounts: CategoryAmounts
    abstract val total: BigDecimal
    abstract val defaultAmount: BigDecimal

    open class FromTotal(
        override val categoryAmounts: CategoryAmounts,
        override val total: BigDecimal,
    ) : CategoryAmountsAndTotal() {
        @delegate:Ignore
        override val defaultAmount by lazy { categoryAmounts.defaultAmount(total) }
    }

    open class FromDefaultAmount(
        override val categoryAmounts: CategoryAmounts,
        override val defaultAmount: BigDecimal,
    ) : CategoryAmountsAndTotal() {
        @delegate:Ignore
        override val total by lazy { categoryAmounts.total(defaultAmount) }
    }

    companion object {
        operator fun invoke(): FromTotal {
            return FromTotal(CategoryAmounts(), BigDecimal.ZERO)
        }

        fun addTogether(categoryAmountsAndTotals: Collection<CategoryAmountsAndTotal>): CategoryAmountsAndTotal =
            addTogether(*categoryAmountsAndTotals.toTypedArray())

        fun addTogether(vararg categoryAmountsAndTotals: CategoryAmountsAndTotal): CategoryAmountsAndTotal {
            return FromTotal(
                CategoryAmounts.addTogether(*categoryAmountsAndTotals.map { it.categoryAmounts }.toTypedArray()),
                categoryAmountsAndTotals.map { it.total }.sum()
            )
        }
    }
}