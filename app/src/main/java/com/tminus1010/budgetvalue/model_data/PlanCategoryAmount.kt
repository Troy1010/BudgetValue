package com.tminus1010.budgetvalue.model_data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity
data class PlanCategoryAmount(
    @PrimaryKey
    override val categoryName: String,
    override val amount: BigDecimal = BigDecimal.ZERO
): ICategoryAmountReceived {
    constructor(category: CategoryDTO, amount: BigDecimal) : this(category.name, amount)
    constructor(tuple: Pair<CategoryDTO, BigDecimal>) : this(tuple.first, tuple.second)
}