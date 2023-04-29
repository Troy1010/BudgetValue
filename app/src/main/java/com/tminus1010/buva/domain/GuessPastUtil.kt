package com.tminus1010.buva.domain

import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal
import java.time.LocalDate

object GuessPastUtil {
    fun guessAccountsTotalInPast(date: LocalDate?, accountsAggregate: AccountsAggregate, transactionBlocks: List<TransactionBlock>, reconciliations: List<Reconciliation>): BigDecimal {
        return accountsAggregate.total
            .minus(
                transactionBlocks
                    .let { if (date == null) listOf() else it.filter { date <= it.datePeriod!!.endDate } }
                    .map { it.total }
                    .sum()
            )
            .minus(
                reconciliations
                    .let { if (date == null) listOf() else it.filter { date <= it.date } }
                    .map { it.total }
                    .sum()
            )
    }

    fun budgettedAmountInPast(category: Category, date: LocalDate?, budgetedCAs: CategoryAmounts, transactionBlocks: List<TransactionBlock>, reconciliations: List<Reconciliation>): BigDecimal {
        return (budgetedCAs[category] ?: BigDecimal.ZERO)
            .minus(
                transactionBlocks
                    .let { if (date == null) listOf() else it.filter { date <= it.datePeriod!!.endDate } }
                    .mapNotNull { it.categoryAmounts[category] }
                    .sum()
            )
            .minus(
                reconciliations
                    .let { if (date == null) listOf() else it.filter { date <= it.date } }
                    .mapNotNull { it.categoryAmounts[category] }
                    .sum()
            )
    }
}