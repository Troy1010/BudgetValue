package com.example.budgetvalue.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity
data class IncomeCategoryAmounts (
    @PrimaryKey
    val category: Category,
    val amount: BigDecimal = BigDecimal.ZERO
)