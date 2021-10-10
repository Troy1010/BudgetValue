package com.tminus1010.budgetvalue.reconcile.app.interactor

import com.tminus1010.budgetvalue.accounts.data.AccountsRepo
import com.tminus1010.budgetvalue.budgeted.BudgetedInteractor
import com.tminus1010.budgetvalue.reconcile.app.CategoryAmountsAndTotal
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class ActiveReconciliationInteractor @Inject constructor(
    accountsRepo: AccountsRepo,
    budgetedInteractor: BudgetedInteractor,
    reconciliationsRepo: ReconciliationsRepo,
) {
    val categoryAmountsAndTotal =
        Observable.combineLatest(accountsRepo.accountsAggregate, budgetedInteractor.budgeted, reconciliationsRepo.activeReconciliationCAs)
        { accountsAggregate, budgeted, activeReconciliationCAs ->
            CategoryAmountsAndTotal.FromTotal(
                activeReconciliationCAs,
                accountsAggregate.total - budgeted.totalAmount
            )
        }
}