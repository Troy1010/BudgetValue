package com.tminus1010.buva.app

import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.domain.CategoryAmountsAndTotal
import com.tminus1010.buva.domain.ReconciliationToDo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn
import java.math.BigDecimal
import javax.inject.Inject

class ActiveReconciliationInteractor @Inject constructor(
    activeReconciliationRepo: ActiveReconciliationRepo,
    reconciliationsToDoInteractor: ReconciliationsToDoInteractor,
    accountsInteractor: AccountsInteractor,
) {
    val categoryAmountsAndTotal =
        combine(activeReconciliationRepo.activeReconciliationCAs, reconciliationsToDoInteractor.currentReconciliationToDo)
        { activeReconciliationCAs, currentReconciliationToDo ->
            CategoryAmountsAndTotal.FromTotal(
                categoryAmounts = activeReconciliationCAs,
                total = when (currentReconciliationToDo) {
                    is ReconciliationToDo.Accounts ->
                        accountsInteractor.guessAccountsTotalInPast(currentReconciliationToDo.date)
                    else -> BigDecimal.ZERO
                },
            )
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)
}