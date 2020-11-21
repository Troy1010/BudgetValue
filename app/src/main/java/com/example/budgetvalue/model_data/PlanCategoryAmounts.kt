package com.example.budgetvalue.model_data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity
data class PlanCategoryAmounts(
    @PrimaryKey
    val category: String,
    val amount: BigDecimal = BigDecimal.ZERO
)