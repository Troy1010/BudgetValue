package com.tminus1010.budgetvalue.model_domain

data class Category (
    val name: String,
    val type: Type,
    val isRequired: Boolean = false
) {
    override fun toString() = name // for better logs.
    enum class Type { Misc, Always, Reservoir }
}