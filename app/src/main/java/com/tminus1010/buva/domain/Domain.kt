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
                    .logx("guessAccountsTotalInPast transactionBlocks")
                    .let { if (date == null) listOf() else it.filter { date <= it.datePeriod!!.endDate } } // TODO: This filter might not be correct.
                    .logx("guessAccountsTotalInPast transactionBlocks filtered")
                    .map { it.total }
                    .logx("guessAccountsTotalInPast transactionBlockTotals")
                    .sum()
            )
            .minus(
                reconciliations
                    .logx("guessAccountsTotalInPast reconciliations")
                    .let { if (date == null) listOf() else it.filter { date <= it.date } }
                    .logx("guessAccountsTotalInPast reconciliations filtered")
                    .map { it.total }
                    .logx("guessAccountsTotalInPast reconciliationsTotals")
                    .sum()
            )
            .logx("guessAccountsTotalInPast result")
    }

    fun sumedCategoryAmountsAndTotalToDate(date: LocalDate?, transactionBlocks: List<TransactionBlock>, reconciliations: List<Reconciliation>): CategoryAmountsAndTotal.FromTotal {
        date.logx("date")
        reconciliations.logx("reconciliations")
        val relevantReconciliations =
            if (date == null)
                reconciliations
            else
                reconciliations.filter { it.date <= date }
        relevantReconciliations.logx("relevantReconciliations")
        transactionBlocks.logx("transactionBlocks")
        val relevantTransactionBlocks =
            if (date == null)
                transactionBlocks
            else
                transactionBlocks.filter { it.datePeriod!!.endDate <= date }
        relevantTransactionBlocks.logx("relevantTransactionBlocks")
        return CategoryAmountsAndTotal.FromTotal(
            categoryAmounts = CategoryAmounts.addTogether(
                *relevantReconciliations.map { it.categoryAmounts }.toTypedArray(),
                *relevantTransactionBlocks.map { it.categoryAmounts }.toTypedArray(),
            ),
            // TODO: This is not always what we want.
            //  Maybe sometimes, like perhaps when we want to calculated the Budgeted column in history, we want to use a guesstimate from the front..
            //  But when it comes to determining if we need an AccountsReconciliation, we need to see if the sumedCA.total from the back matches the guesstimate from the front.
            total = relevantReconciliations.map { it.total }.sum()
                .plus(relevantTransactionBlocks.map { it.total }.sum()),
        ).also { it.total.logx("totalqqqwwww") }
    }
}