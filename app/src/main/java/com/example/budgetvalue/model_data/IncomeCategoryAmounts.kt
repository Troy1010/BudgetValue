package com.example.budgetvalue.model_data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.budgetvalue.model_app.Category
import java.math.BigDecimal

@Entity
data class IncomeCategoryAmounts (
    @PrimaryKey
    val category: Category,
    val amount: BigDecimal = BigDecimal.ZERO
)