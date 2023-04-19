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
    val datePeriod: LocalDatePeriod?,
    val transactions: List<Transaction>,
    val isFullyImported: Boolean,
) : CategoryAmountsAndTotal.FromTotal(
    transactions.fold(CategoryAmounts()) { acc, transaction -> acc.addTogether(transaction.categoryAmounts) },
    transactions.map { it.amount }.sum()
), Parcelable {
    /**
     * the nothing variable is a workaround so that the constructor doesn't have an overload conflict.
     */
    constructor(datePeriod: LocalDatePeriod?, unfilteredTransactions: List<Transaction>, isFullyImported: Boolean, nothing: Unit? = null) : this(datePeriod, if (datePeriod == null) unfilteredTransactions else unfilteredTransactions.filter { it.date in datePeriod }.sortedBy { it.date }, isFullyImported)

    val size get() = transactions.size
    val spendBlock get() = TransactionBlock(datePeriod, transactions.filter { it.isSpend }, isFullyImported)
    val incomeBlock get() = TransactionBlock(datePeriod, transactions.filter { it.isIncome }, isFullyImported)

    @IgnoredOnParcel
    val percentageOfCategorizedTransactions = transactions.count { it.isCategorized }.toFloat() / transactions.count()
    val isFullyCategorized get() = defaultAmount.isZero
}