package com.example.budgetvalue.model_data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity
data class ReconcileCategoryAmounts(
    @PrimaryKey
    val categoryName: String,
    val amount: BigDecimal = BigDecimal.ZERO
)