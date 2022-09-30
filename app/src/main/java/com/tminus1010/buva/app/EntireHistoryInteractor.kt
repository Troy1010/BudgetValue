package com.tminus1010.buva.app

import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.domain.addTogether
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntireHistoryInteractor @Inject constructor(
    transactionsInteractor: TransactionsInteractor,
    reconciliationsRepo: ReconciliationsRepo,
) {
    val categoryAmountsAndTotal =
        combine(transactionsInteractor.transactionBlocks, reconciliationsRepo.reconciliations)
        { transactionBlocks, reconciliations -> (transactionBlocks + reconciliations).addTogether() }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)
}