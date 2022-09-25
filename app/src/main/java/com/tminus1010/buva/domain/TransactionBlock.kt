package com.tminus1010.buva.domain

import android.os.Parcelable
import com.tminus1010.buva.all_layers.extensions.isZero
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * a null [datePeriod] represents forever
 */
@Parcelize
data class TransactionBlock private constructor(
    private val transactions: List<Transaction>,
    val datePeriod: LocalDatePeriod?,
) : CategoryAmountsAndTotal.FromTotal(
    transactions.fold(CategoryAmounts()) { acc, transaction -> acc.addTogether(transaction.categoryAmounts) },
    transactions.map { it.amount }.sum()
), Parcelable {
    /**
     * the nothing variable is a workaround so that the constructor doesn't have an overload conflict.
     */
    constructor(unfilteredTransactions: List<Transaction>, datePeriod: LocalDatePeriod?, nothing: Unit? = null) : this(if (datePeriod == null) unfilteredTransactions else unfilteredTransactions.filter { it.date in datePeriod }, datePeriod)

    @IgnoredOnParcel
    val size = transactions.size
    val spendBlock get() = TransactionBlock(transactions.filter { it.isSpend }, datePeriod)
    val incomeBlock get() = TransactionBlock(transactions.filter { it.isIncome }, datePeriod)

    @IgnoredOnParcel
    val percentageOfCategorizedTransactions = transactions.count { it.isCategorized }.toFloat() / transactions.count()
    val isFullyCategorized get() = defaultAmount.isZero
    val isFullyImported: Boolean get() = true // TODO()
}