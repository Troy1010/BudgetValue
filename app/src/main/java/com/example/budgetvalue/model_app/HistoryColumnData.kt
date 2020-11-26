package com.example.budgetvalue.model_app

import java.math.BigDecimal
import java.time.LocalDate

data class HistoryColumnData(
    val title: String,
    val categoryAmounts: Map<Category, BigDecimal>
) {
    constructor(subTitle: String, x: LocalDate, categoryAmounts: Map<Category, BigDecimal>): this(getTitle(subTitle, x), categoryAmounts)
    companion object {
        fun getTitle(subTitle: String, x: LocalDate): String {
            return "$subTitle\n$x"
        }
    }
}