package com.tminus1010.budgetvalue.categories.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity
data class CategoryDTO (
    @PrimaryKey
    val name: String,
    val type: Int,
    val defaultAmount: BigDecimal,
    val isRequired: Boolean = false,
)