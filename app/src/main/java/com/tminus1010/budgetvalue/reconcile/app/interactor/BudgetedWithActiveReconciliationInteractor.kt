package com.tminus1010.budgetvalue.reconcile.app.interactor

import com.tminus1010.budgetvalue.budgeted.BudgetedInteractor
import com.tminus1010.budgetvalue.reconcile.app.CategoryAmountsAndTotal
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class BudgetedWithActiveReconciliationInteractor @Inject constructor(
    budgetedInteractor: BudgetedInteractor,
    activeReconciliationInteractor: ActiveReconciliationInteractor,
) {
    val categoryAmountsAndTotal =
        Observable.combineLatest(budgetedInteractor.budgeted, activeReconciliationInteractor.categoryAmountsAndTotal)
        { budgeted, activeReconciliation ->
            CategoryAmountsAndTotal.FromTotal(
                activeReconciliation.categoryAmounts.addTogether(budgeted.categoryAmounts),
                budgeted.totalAmount + activeReconciliation.total
            )
        }
}