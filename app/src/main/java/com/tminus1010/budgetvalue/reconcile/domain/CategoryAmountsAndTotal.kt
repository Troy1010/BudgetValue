package com.tminus1010.budgetvalue.reconcile.domain

import com.tminus1010.budgetvalue._core.app.CategoryAmounts
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

        override val defaultAmount by lazy { categoryAmounts.defaultAmount(total) }
    }

    open class FromDefaultAmount(
        override val categoryAmounts: CategoryAmounts,
        override val defaultAmount: BigDecimal,
    ) : CategoryAmountsAndTotal() {
        constructor(categoryAmounts: Map<Category, BigDecimal>, total: BigDecimal) : this(CategoryAmounts(categoryAmounts), total)

        override val total by lazy { categoryAmounts.total(defaultAmount) }
    }
}