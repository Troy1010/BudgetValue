package com.example.budgetvalue.layers.z_ui.misc

import com.example.budgetvalue.models.Category
import java.math.BigDecimal

data class SplitRowData (
    val category: Category,
    val split: BigDecimal,
    val income: BigDecimal
) {
    val budgeted = split + income
    fun toListStr(): List<String> {
        return listOf(category.name, split.toString(), income.toString(), budgeted.toString())
    }
}