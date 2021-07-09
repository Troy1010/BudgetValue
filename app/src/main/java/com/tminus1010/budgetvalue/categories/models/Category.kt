package com.tminus1010.budgetvalue.categories.models

import java.math.BigDecimal

data class Category(
    val name: String,
    val type: CategoryType = CategoryType.Always,
    val defaultAmount: BigDecimal = BigDecimal.ZERO,
    val isRequired: Boolean = false,
) {
    fun toDTO() =
        CategoryDTO(name, type.ordinal, defaultAmount, isRequired)

    companion object {
        fun fromDTO(categoryDTO: CategoryDTO) = categoryDTO.run {
            Category(name, CategoryType.values()[type], defaultAmount, isRequired)
        }
    }
}