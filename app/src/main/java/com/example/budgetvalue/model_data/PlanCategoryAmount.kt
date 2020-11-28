package com.example.budgetvalue.model_data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.budgetvalue.model_app.Category
import java.math.BigDecimal

@Entity
data class PlanCategoryAmount(
    @PrimaryKey
    override val categoryName: String,
    override val amount: BigDecimal = BigDecimal.ZERO
): ICategoryAmountReceived {
    constructor(category: Category, amount: BigDecimal) : this(category.name, amount)
}