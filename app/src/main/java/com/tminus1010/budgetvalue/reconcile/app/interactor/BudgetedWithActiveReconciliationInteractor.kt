package com.tminus1010.budgetvalue.reconcile.app.interactor

import com.tminus1010.budgetvalue.all_features.all_layers.extensions.easyEquals
import com.tminus1010.budgetvalue.budgeted.BudgetedInteractor
import com.tminus1010.budgetvalue.reconcile.domain.BudgetedWithActiveReconciliation
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject

class BudgetedWithActiveReconciliationInteractor @Inject constructor(
    budgetedInteractor: BudgetedInteractor,
    activeReconciliationInteractor: ActiveReconciliationInteractor,
) {
    val categoryAmountsAndTotal =
        Observable.combineLatest(budgetedInteractor.budgeted, activeReconciliationInteractor.categoryAmountsAndTotal)
        { budgeted, activeReconciliation ->
            BudgetedWithActiveReconciliation(
                categoryAmounts = activeReconciliation.categoryAmounts.addTogether(budgeted.categoryAmounts),
                total = budgeted.totalAmount + activeReconciliation.total,
                caValidation = { it ?: BigDecimal.ZERO >= BigDecimal.ZERO },
                defaultAmountValidation = { (it ?: BigDecimal.ZERO).easyEquals(BigDecimal.ZERO) }
            )
        }
}