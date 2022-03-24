package com.tminus1010.budgetvalue._unrestructured.transactions.app

import com.tminus1010.budgetvalue.all_layers.extensions.isZero
import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.domain.LocalDatePeriod
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import com.tminus1010.tmcommonkotlin.tuple.Box

/**
 * a null [datePeriod] represents forever
 */
data class TransactionBlock(
    private val _transactionSet: List<Transaction>,
    val datePeriod: LocalDatePeriod?,
) {
    constructor(_transactionSet: List<Transaction>, datePeriodBox: Box<LocalDatePeriod?>) : this(_transactionSet, datePeriodBox.first)

    val transactionSet = if (datePeriod == null) _transactionSet else _transactionSet.filter { it.date in datePeriod }
    val amount = transactionSet.map { it.amount }.sum()!!
    val size = transactionSet.size
    val defaultAmount get() = amount - categoryAmounts.values.sum()
    val categoryAmounts =
        transactionSet
            .fold(CategoryAmounts()) { acc, transaction -> acc.addTogether(transaction.categoryAmounts) }
    val spendBlock get() = TransactionBlock(transactionSet.filter { it.isSpend }, datePeriod)
    val percentageOfCategorizedTransactions = _transactionSet.filter { it.isCategorized }.count().toFloat() / _transactionSet.count()
    val isFullyCategorized get() = defaultAmount.isZero
    val isFullyImported: Boolean get() = true // TODO()
}