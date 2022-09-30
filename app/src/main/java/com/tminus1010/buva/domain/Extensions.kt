package com.tminus1010.buva.domain

import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import java.math.BigDecimal

fun List<TransactionBlock>.averagedCategoryAmounts() =
    fold(CategoryAmounts()) { acc, v -> acc.addTogether(v.categoryAmounts) }
        .mapValues { (_, v) -> (-v / size.toBigDecimal()).toString().toMoneyBigDecimal() }
        .let { CategoryAmounts(it) }

fun List<TransactionBlock>.averagedTotal() =
    (fold(BigDecimal.ZERO) { acc, v -> acc + v.total } / size.toBigDecimal().toString().toMoneyBigDecimal())
        .toString().toMoneyBigDecimal()

fun Collection<CategoryAmountsAndTotal>.addTogether() =
    CategoryAmountsAndTotal.addTogether(this)