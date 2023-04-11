package com.tminus1010.buva.app

import com.tminus1010.buva.data.AccountsRepo
import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.domain.AutomaticBalanceReconciliation
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AutomaticBalanceInteractor @Inject constructor(
    private val transactionsInteractor: TransactionsInteractor,
    private val reconciliationsRepo: ReconciliationsRepo,
    private val accountsRepo: AccountsRepo,
) {
    val total =
        combine(transactionsInteractor.transactionBlocks, reconciliationsRepo.reconciliations, accountsRepo.accountsAggregate)
        { transactionBlocks, reconciliations, accountsAggregate ->
            accountsAggregate.total - transactionBlocks.map { it.total }.plus(reconciliations.map { it.total }).sum()
        }
    val automaticBalanceReconciliation =
        total
            .map {
                AutomaticBalanceReconciliation(
                    CategoryAmounts(),
                    it,
                )
            }
}