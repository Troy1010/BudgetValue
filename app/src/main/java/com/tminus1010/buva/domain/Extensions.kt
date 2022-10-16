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

fun Iterable<LocalDatePeriod>.mergeOverlapping(): List<LocalDatePeriod> {
    val currentPeriods = sortedBy { it.startDate }.toMutableList()
    var done = false
    while (!done) {
        var result: Pair<LocalDatePeriod, LocalDatePeriod>? = null
        for (x in currentPeriods) {
            val currentPeriodsWithoutX = currentPeriods.toMutableList().apply { remove(x) }
            result = currentPeriodsWithoutX.find { it.startDate < x.endDate }?.let { Pair(x, it) }
            if (result != null) break
        }
        if (result != null) {
            val firstPeriodToRemove = result.first
            val secondPeriodToRemove = result.second
            val newPeriod = LocalDatePeriod(firstPeriodToRemove.startDate, secondPeriodToRemove.endDate)
            currentPeriods[currentPeriods.indexOf(firstPeriodToRemove)] = newPeriod
            currentPeriods.remove(secondPeriodToRemove)
        } else {
            done = true
        }
    }
    return currentPeriods.toList()
}

fun Map<Category, BigDecimal>.toCategoryAmounts(): CategoryAmounts {
    return CategoryAmounts(this)
}