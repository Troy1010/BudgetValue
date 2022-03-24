package com.tminus1010.budgetvalue.reconcile.app.interactor

import com.tminus1010.budgetvalue.all_features.all_layers.extensions.asObservable2
import com.tminus1010.budgetvalue.all_features.data.AccountsRepo
import com.tminus1010.budgetvalue.all_features.app.BudgetedInteractor
import com.tminus1010.budgetvalue.reconcile.data.ActiveReconciliationRepo
import com.tminus1010.budgetvalue.reconcile.domain.CategoryAmountsAndTotal
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class ActiveReconciliationInteractor @Inject constructor(
    accountsRepo: AccountsRepo,
    budgetedInteractor: BudgetedInteractor,
    activeReconciliationRepo: ActiveReconciliationRepo,
) {
    val categoryAmountsAndTotal =
        Observable.combineLatest(accountsRepo.accountsAggregate.asObservable2(), budgetedInteractor.budgeted, activeReconciliationRepo.activeReconciliationCAs.asObservable2())
        { accountsAggregate, budgeted, activeReconciliationCAs ->
            CategoryAmountsAndTotal.FromTotal(
                activeReconciliationCAs,
                accountsAggregate.total - budgeted.totalAmount,
            )
        }
}