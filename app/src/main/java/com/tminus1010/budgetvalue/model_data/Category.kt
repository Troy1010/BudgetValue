package com.tminus1010.budgetvalue.model_data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category (
    @PrimaryKey
    val name: String,
    val type: Type,
    val isRequired: Boolean = false
) {
    override fun toString() = name
    enum class Type { Default, Always, Reservoir }
}