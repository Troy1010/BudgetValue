package com.tminus1010.budgetvalue.reconcile.app.interactor

import com.tminus1010.budgetvalue._core.all.extensions.asObservable2
import com.tminus1010.budgetvalue.accounts.data.AccountsRepo
import com.tminus1010.budgetvalue.budgeted.BudgetedInteractor
import com.tminus1010.budgetvalue.reconcile.data.ActiveReconciliationRepo
import com.tminus1010.budgetvalue.reconcile.domain.CategoryAmountsAndTotal
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class ActiveReconciliationInteractor @Inject constructor(
    accountsRepo: AccountsRepo,
    budgetedInteractor: BudgetedInteractor,
    activeReconciliationRepo: ActiveReconciliationRepo,
) {
    val categoryAmountsAndTotal =
        Observable.combineLatest(accountsRepo.accountsAggregate, budgetedInteractor.budgeted, activeReconciliationRepo.activeReconciliationCAs.asObservable2())
        { accountsAggregate, budgeted, activeReconciliationCAs ->
            CategoryAmountsAndTotal.FromTotal(
                activeReconciliationCAs,
                accountsAggregate.total - budgeted.totalAmount,
            )
        }
}