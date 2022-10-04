package com.tminus1010.buva.app

import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.domain.CategoryAmountsAndTotals
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import javax.inject.Inject

class HistoryInteractor @Inject constructor(
    transactionsInteractor: TransactionsInteractor,
    reconciliationsRepo: ReconciliationsRepo,
    budgetedVsAccountsAutomaticReconciliationInteractor: BudgetedVsAccountsAutomaticReconciliationInteractor,
) {
    val entireHistory =
        combine(transactionsInteractor.transactionBlocks, reconciliationsRepo.reconciliations, budgetedVsAccountsAutomaticReconciliationInteractor.budgetedVsAccountsAutomaticReconciliation, ::Triple)
            .sample(500)
            .distinctUntilChanged()
            .map { (transactionBlocks, reconciliations, budgetedVsAccountsAutomaticReconciliation) ->
                CategoryAmountsAndTotals(
                    categoryAmountsAndTotals = transactionBlocks
                        .plus(reconciliations)
                        .plus(budgetedVsAccountsAutomaticReconciliation)
                )
            }
}