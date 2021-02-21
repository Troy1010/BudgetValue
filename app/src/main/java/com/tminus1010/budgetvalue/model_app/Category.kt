package com.tminus1010.budgetvalue.model_app

data class Category (
    val name: String,
    val type: Type,
    val isRequired: Boolean = false
) {
    enum class Type { Default, Always, Reservoir }
}