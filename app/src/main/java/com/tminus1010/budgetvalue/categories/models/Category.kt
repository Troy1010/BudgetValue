package com.tminus1010.budgetvalue.categories.models

import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import java.math.BigDecimal

data class Category(
    val name: String,
    val type: CategoryType = CategoryType.Always,
    val defaultAmountFormula: AmountFormula = AmountFormula.Value(BigDecimal.ZERO),
    val isRequired: Boolean = false,
) {
    override fun toString() = name // for logs
    fun toDTO() = CategoryDTO(name, type.ordinal, defaultAmountFormula.toDTO(), isRequired)

    companion object {
        fun fromDTO(categoryDTO: CategoryDTO) = categoryDTO.run {
            Category(name, CategoryType.values()[type], AmountFormula.fromDTO(defaultAmountFormulaStr), isRequired)
        }
    }
}