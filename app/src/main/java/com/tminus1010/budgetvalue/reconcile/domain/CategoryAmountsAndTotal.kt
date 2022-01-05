package com.tminus1010.budgetvalue.reconcile.domain

import androidx.room.Ignore
import com.tminus1010.budgetvalue._core.domain.CategoryAmounts
import com.tminus1010.budgetvalue.categories.models.Category
import java.math.BigDecimal

sealed class CategoryAmountsAndTotal {
    abstract val categoryAmounts: CategoryAmounts
    abstract val total: BigDecimal
    abstract val defaultAmount: BigDecimal

    open class FromTotal(
        override val categoryAmounts: CategoryAmounts,
        override val total: BigDecimal,
    ) : CategoryAmountsAndTotal() {
        constructor(categoryAmounts: Map<Category, BigDecimal>, total: BigDecimal) : this(CategoryAmounts(categoryAmounts), total)

        @delegate:Ignore
        override val defaultAmount by lazy { categoryAmounts.defaultAmount(total) }
    }

    open class FromDefaultAmount(
        override val categoryAmounts: CategoryAmounts,
        override val defaultAmount: BigDecimal,
    ) : CategoryAmountsAndTotal() {
        constructor(categoryAmounts: Map<Category, BigDecimal>, total: BigDecimal) : this(CategoryAmounts(categoryAmounts), total)

        @delegate:Ignore
        override val total by lazy { categoryAmounts.total(defaultAmount) }
    }
}