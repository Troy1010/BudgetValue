package com.tminus1010.budgetvalue.model_data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryDTO (
    @PrimaryKey
    val name: String,
    val type: String,
    val isRequired: Boolean = false
)