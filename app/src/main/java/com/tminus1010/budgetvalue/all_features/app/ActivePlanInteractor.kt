package com.tminus1010.budgetvalue.all_features.app

import com.tminus1010.budgetvalue.all_features.all_layers.extensions.isZero
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.all_features.domain.CategoryAmounts
import com.tminus1010.budgetvalue.all_features.data.repo.ActivePlanRepo
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ActivePlanInteractor @Inject constructor(
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