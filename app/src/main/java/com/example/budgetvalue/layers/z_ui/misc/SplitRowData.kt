package com.example.budgetvalue.layers.z_ui.misc

import com.example.budgetvalue.models.Category
import java.math.BigDecimal

data class SplitRowData (
    val category: Category,
    val spent: BigDecimal,
    val income: BigDecimal
) {
    val budgeted = spent + income
}