package com.tminus1010.budgetvalue.domain

import com.tminus1010.budgetvalue.all_layers.extensions.isZero
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import com.tminus1010.tmcommonkotlin.tuple.Box

/**
 * a null [datePeriod] represents forever
 */
data class TransactionBlock(
    private val _transactionSet: List<Transaction>,
    val datePeriod: LocalDatePeriod?,
) : CategoryAmountsAndTotal.FromTotal(
    _transactionSet.fold(CategoryAmounts()) { acc, transaction -> acc.addTogether(transaction.categoryAmounts) },
    _transactionSet.map { it.amount }.sum()
) {
    constructor(_transactionSet: List<Transaction>, datePeriodBox: Box<LocalDatePeriod?>) : this(_transactionSet, datePeriodBox.first)

    val transactions = if (datePeriod == null) _transactionSet else _transactionSet.filter { it.date in datePeriod }
    val size = transactions.size
    val spendBlock get() = TransactionBlock(transactions.filter { it.isSpend }, datePeriod)
    val percentageOfCategorizedTransactions = _transactionSet.filter { it.isCategorized }.count().toFloat() / _transactionSet.count()
    val isFullyCategorized get() = defaultAmount.isZero
    val isFullyImported: Boolean get() = true // TODO()
}