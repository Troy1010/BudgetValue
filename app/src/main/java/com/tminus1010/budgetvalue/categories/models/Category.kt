package com.tminus1010.budgetvalue.categories.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
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
}