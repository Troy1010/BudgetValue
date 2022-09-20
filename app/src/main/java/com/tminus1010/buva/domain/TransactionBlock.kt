package com.tminus1010.buva.domain

import com.tminus1010.buva.all_layers.extensions.isZero
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import com.tminus1010.tmcommonkotlin.tuple.Box

/**
 * a null [datePeriod] represents forever
 */
data class TransactionBlock(
    private val unsortedTransactions: List<Transaction>,
    val datePeriod: LocalDatePeriod?,
) : CategoryAmountsAndTotal.FromTotal(
    unsortedTransactions.fold(CategoryAmounts()) { acc, transaction -> acc.addTogether(transaction.categoryAmounts) },
    unsortedTransactions.map { it.amount }.sum()
) {
    constructor(_transactionSet: List<Transaction>, datePeriodBox: Box<LocalDatePeriod?>) : this(_transactionSet, datePeriodBox.first)

    val transactions = if (datePeriod == null) unsortedTransactions else unsortedTransactions.filter { it.date in datePeriod }
    val size = transactions.size
    val spendBlock get() = TransactionBlock(transactions.filter { it.isSpend }, datePeriod)
    val incomeBlock get() = TransactionBlock(transactions.filter { it.isIncome }, datePeriod)
    val percentageOfCategorizedTransactions = unsortedTransactions.filter { it.isCategorized }.count().toFloat() / unsortedTransactions.count()
    val isFullyCategorized get() = defaultAmount.isZero
    val isFullyImported: Boolean get() = true // TODO()
}