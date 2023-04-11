package com.tminus1010.buva.app

import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.domain.CategoryAmountsAndTotalsAggregate
import com.tminus1010.tmcommonkotlin.tuple.createTuple
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import javax.inject.Inject

class HistoryInteractor @Inject constructor(
    private val transactionsInteractor: TransactionsInteractor,
    private val reconciliationsRepo: ReconciliationsRepo,
    private val automaticBalanceInteractor: AutomaticBalanceInteractor,
) {
    val entireHistory =
        combine(transactionsInteractor.transactionBlocks, reconciliationsRepo.reconciliations, automaticBalanceInteractor.automaticBalanceReconciliation, ::createTuple)
            .sample(500)
            .distinctUntilChanged()
            .map { (transactionBlocks, reconciliations, budgetedVsAccountsAutomaticReconciliation) ->
                CategoryAmountsAndTotalsAggregate(
                    categoryAmountsAndTotals = transactionBlocks
                        .plus(reconciliations)
                        .plus(budgetedVsAccountsAutomaticReconciliation)
                )
            }
}