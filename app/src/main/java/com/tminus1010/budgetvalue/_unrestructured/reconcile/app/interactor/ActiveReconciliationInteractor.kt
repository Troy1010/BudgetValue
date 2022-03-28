package com.tminus1010.budgetvalue._unrestructured.reconcile.app.interactor

import com.tminus1010.budgetvalue.app.BudgetedInteractor
import com.tminus1010.budgetvalue.data.AccountsRepo
import com.tminus1010.budgetvalue.data.ActiveReconciliationRepo
import com.tminus1010.budgetvalue.domain.CategoryAmountsAndTotal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.rx3.asFlow
import javax.inject.Inject

class ActiveReconciliationInteractor @Inject constructor(
    accountsRepo: AccountsRepo,
    budgetedInteractor: BudgetedInteractor,
    activeReconciliationRepo: ActiveReconciliationRepo,
) {
    val categoryAmountsAndTotal =
        combine(accountsRepo.accountsAggregate, budgetedInteractor.budgeted.asFlow(), activeReconciliationRepo.activeReconciliationCAs)
        { accountsAggregate, budgeted, activeReconciliationCAs ->
            CategoryAmountsAndTotal.FromTotal(
                categoryAmounts = activeReconciliationCAs,
                total = accountsAggregate.total - budgeted.totalAmount,
            )
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)
}