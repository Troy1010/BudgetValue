package com.tminus1010.budgetvalue.plans.app.interactor

import com.tminus1010.budgetvalue._core.all.extensions.isZero
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.domain.CategoryAmounts
import com.tminus1010.budgetvalue.plans.data.ActivePlanRepo
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SetActivePlanFromHistoryInteractor @Inject constructor(
    private val activePlanRepo: ActivePlanRepo,
    private val transactionsInteractor: TransactionsInteractor,
) {
    suspend fun setActivePlanFromHistory() {
        val relevantTransactionBlocks = transactionsInteractor.spendBlocks2.first().filter { it.defaultAmount.isZero }
        val categoryAmounts =
            relevantTransactionBlocks
                .fold(CategoryAmounts()) { acc, v -> acc.addTogether(v.categoryAmounts) }
                .mapValues { (_, v) -> (-v / relevantTransactionBlocks.size.toBigDecimal()).toString().toMoneyBigDecimal() }
        activePlanRepo.pushCategoryAmounts(CategoryAmounts(categoryAmounts))
    }
}