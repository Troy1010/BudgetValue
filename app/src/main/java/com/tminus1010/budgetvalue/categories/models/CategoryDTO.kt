package com.tminus1010.budgetvalue.categories.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryDTO(
    @PrimaryKey
    val name: String,
    val type: Int,
    val defaultAmountFormulaStr: String,
    val isRequired: Boolean = false,
)