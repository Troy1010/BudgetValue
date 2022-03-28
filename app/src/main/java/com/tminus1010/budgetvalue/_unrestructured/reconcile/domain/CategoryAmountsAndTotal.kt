package com.tminus1010.budgetvalue._unrestructured.reconcile.domain

import androidx.room.Ignore
import com.tminus1010.budgetvalue.domain.CategoryAmounts
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
}