package com.tminus1010.budgetvalue.all_features.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity
data class Category(
    @PrimaryKey
    val name: String,
    val type: CategoryType = CategoryType.Always,
    val defaultAmountFormula: AmountFormula = AmountFormula.Value(BigDecimal.ZERO),
    val isRequired: Boolean = false,
) {
    override fun toString() = name // for logs

    companion object {
        val DEFAULT = Category("Default", CategoryType.Special, AmountFormula.Value(BigDecimal.ZERO), true)
        val UNRECOGNIZED = Category("Unrecognized", CategoryType.Special, AmountFormula.Value(BigDecimal.ZERO), true)
    }
}