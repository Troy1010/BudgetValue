package com.tminus1010.buva.domain

import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal
import java.time.LocalDate

object Domain {
    fun shouldSkip(reconciliationSkips: List<ReconciliationSkip>, transactionBlock: TransactionBlock, anchorDateOffset: Long): Boolean {
        if (transactionBlock.datePeriod == null) return false
        return reconciliationSkips.any { it.localDate(anchorDateOffset) in transactionBlock.datePeriod }
    }

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

    fun sumedCategoryAmountsAndTotalToDate(date: LocalDate?, transactionBlocks: List<TransactionBlock>, reconciliations: List<Reconciliation>): CategoryAmountsAndTotal.FromTotal {
        val relevantReconciliations =
            if (date == null)
                reconciliations
            else
                reconciliations.filter { it.date <= date }
        val relevantTransactionBlocks =
            if (date == null)
                transactionBlocks
            else
                transactionBlocks.filter { it.datePeriod!!.endDate <= date }
        return CategoryAmountsAndTotal.FromTotal(
            categoryAmounts = CategoryAmounts.addTogether(
                *relevantReconciliations.map { it.categoryAmounts }.toTypedArray(),
                *relevantTransactionBlocks.map { it.categoryAmounts }.toTypedArray(),
            ),
            total = relevantReconciliations.map { it.total }.sum()
                .plus(relevantTransactionBlocks.map { it.total }.sum()),
        )
    }

    fun isPeriodFullyImported(datePeriod: LocalDatePeriod, transactionImportInfos: List<TransactionImportInfo>): Boolean {
        return transactionImportInfos.map { it.period }
            .mergeOverlapping()
            .any { datePeriod in it }
    }
}