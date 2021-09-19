package com.tminus1010.budgetvalue.all.domain

import com.tminus1010.budgetvalue._core.extensions.isZero
import com.tminus1010.budgetvalue._core.domain.LocalDatePeriod
import com.tminus1010.budgetvalue._core.models.CategoryAmounts
import com.tminus1010.budgetvalue._shared.date_period_getter.IDatePeriodGetter
import com.tminus1010.budgetvalue.history.models.IHistoryColumnData
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.tmcommonkotlin.misc.extensions.sum

data class TransactionBlock(
    private val transactionSet: List<Transaction>,
    val datePeriod: LocalDatePeriod,
) : IHistoryColumnData {
    val amount = transactionSet.map { it.amount }.sum()!!
    override val title = "Actual"
    override fun subTitle(datePeriodGetter: IDatePeriodGetter): String? {
        TODO("Not yet implemented")
    }

    val size = transactionSet.size
    override val defaultAmount get() = amount - categoryAmounts.values.sum()
    override val categoryAmounts: CategoryAmounts =
        transactionSet
            .fold(CategoryAmounts()) { acc, transaction -> acc.addTogether(transaction.categoryAmounts) }

    val spendBlock get() = TransactionBlock(transactionSet.filter { it.isSpend }, datePeriod)

    val isFullyCategorized get() = defaultAmount.isZero
    val isFullyImported: Boolean get() = TODO()
}