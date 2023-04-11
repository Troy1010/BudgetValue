package com.tminus1010.buva.domain

class CategoryAmountsAndTotalsAggregate(
    private val categoryAmountsAndTotals: List<CategoryAmountsAndTotal>,
) : List<CategoryAmountsAndTotal> by categoryAmountsAndTotals {
    val addedTogether by lazy { categoryAmountsAndTotals.addTogether() }
}