package com.tminus1010.buva.app

import com.tminus1010.buva.data.AccountsRepo
import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.domain.BudgetedVsAccountsAutomaticReconciliation
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class BudgetedVsAccountsAutomaticReconciliationInteractor @Inject constructor(
    private val transactionsInteractor: TransactionsInteractor,
    private val reconciliationsRepo: ReconciliationsRepo,
    private val accountsRepo: AccountsRepo,
) {
    val budgetedVsAccountsAutomaticReconciliation =
        combine(transactionsInteractor.transactionBlocks, reconciliationsRepo.reconciliations, accountsRepo.accountsAggregate)
        { transactionBlocks, reconciliations, accountsAggregate ->
            val totalFromHistory = transactionBlocks.map { it.total }.plus(reconciliations.map { it.total }).sum()
            BudgetedVsAccountsAutomaticReconciliation(
                CategoryAmounts(),
                accountsAggregate.total - totalFromHistory,
            )
        }
}