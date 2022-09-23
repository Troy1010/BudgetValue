package com.tminus1010.buva.domain

import com.tminus1010.buva.all_layers.extensions.isZero
import com.tminus1010.tmcommonkotlin.misc.extensions.sum

/**
 * a null [datePeriod] represents forever
 */
data class TransactionBlock private constructor(
    private val transactions: List<Transaction>,
    val datePeriod: LocalDatePeriod?,
) : CategoryAmountsAndTotal.FromTotal(
    transactions.fold(CategoryAmounts()) { acc, transaction -> acc.addTogether(transaction.categoryAmounts) },
    transactions.map { it.amount }.sum()
) {
    val size = transactions.size
    val spendBlock get() = TransactionBlock(transactions.filter { it.isSpend }, datePeriod)
    val incomeBlock get() = TransactionBlock(transactions.filter { it.isIncome }, datePeriod)
    val percentageOfCategorizedTransactions = transactions.count { it.isCategorized }.toFloat() / transactions.count()
    val isFullyCategorized get() = defaultAmount.isZero
    val isFullyImported: Boolean get() = true // TODO()

    companion object {
        fun create(unfilteredTransactions: List<Transaction>, datePeriod: LocalDatePeriod?): TransactionBlock {
            return TransactionBlock(if (datePeriod == null) unfilteredTransactions else unfilteredTransactions.filter { it.date in datePeriod }, datePeriod)
        }
    }
}