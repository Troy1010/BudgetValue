package com.tminus1010.budgetvalue.categories.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryDTO (
    @PrimaryKey
    val name: String,
    val type: String,
    val isRequired: Boolean = false
)