package com.tminus1010.budgetvalue._unrestructured.reconcile.app.interactor

import com.tminus1010.budgetvalue.all_layers.extensions.asObservable2
import com.tminus1010.budgetvalue.data.AccountsRepo
import com.tminus1010.budgetvalue.app.BudgetedInteractor
import com.tminus1010.budgetvalue.data.ActiveReconciliationRepo
import com.tminus1010.budgetvalue.domain.CategoryAmountsAndTotal
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