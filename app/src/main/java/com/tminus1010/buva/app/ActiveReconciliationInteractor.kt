package com.tminus1010.buva.app

import com.tminus1010.buva.data.AccountsRepo
import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.domain.CategoryAmountsAndTotal
import com.tminus1010.buva.domain.Domain
import com.tminus1010.buva.domain.ReconciliationToDo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import java.math.BigDecimal
import javax.inject.Inject

class ActiveReconciliationInteractor @Inject constructor(
    activeReconciliationRepo: ActiveReconciliationRepo,
    reconciliationsToDoInteractor: ReconciliationsToDoInteractor,
    accountsRepo: AccountsRepo,
    transactionsInteractor: TransactionsInteractor,
    reconciliationsRepo: ReconciliationsRepo,
) {
    val categoryAmountsAndTotal =
        combine(activeReconciliationRepo.activeReconciliationCAs, reconciliationsToDoInteractor.currentReconciliationToDo, accountsRepo.accountsAggregate, transactionsInteractor.transactionBlocks, reconciliationsRepo.reconciliations)
        { activeReconciliationCAs, currentReconciliationToDo, accountsAggregate, transactionBlocks, reconciliations ->
            CategoryAmountsAndTotal.FromTotal(
                categoryAmounts = activeReconciliationCAs,
                total = when (currentReconciliationToDo) {
                    is ReconciliationToDo.Accounts ->
                        Domain.guessAccountsTotalInPast(currentReconciliationToDo.date, accountsAggregate, transactionBlocks, reconciliations)
                    else -> BigDecimal.ZERO
                },
            )
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    val targetDefaultAmount =
        reconciliationsToDoInteractor.currentReconciliationToDo.map { currentReconciliationToDo ->
            when (currentReconciliationToDo) {
                is ReconciliationToDo.PlanZ ->
                    -currentReconciliationToDo.transactionBlock.incomeBlock.total
                else ->
                    BigDecimal.ZERO
            }
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)
}