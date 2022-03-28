package com.tminus1010.budgetvalue._unrestructured.reconcile.app.interactor

import com.tminus1010.budgetvalue.all_layers.extensions.easyEquals
import com.tminus1010.budgetvalue._unrestructured.reconcile.domain.BudgetedWithActiveReconciliation
import com.tminus1010.budgetvalue.app.BudgetedInteractor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.rx3.asFlow
import java.math.BigDecimal
import javax.inject.Inject

class BudgetedWithActiveReconciliationInteractor @Inject constructor(
    budgetedInteractor: BudgetedInteractor,
    activeReconciliationInteractor: ActiveReconciliationInteractor,
) {
    val categoryAmountsAndTotal =
        combine(budgetedInteractor.budgeted.asFlow(), activeReconciliationInteractor.categoryAmountsAndTotal)
        { budgeted, activeReconciliation ->
            BudgetedWithActiveReconciliation(
                categoryAmounts = activeReconciliation.categoryAmounts.addTogether(budgeted.categoryAmounts),
                total = budgeted.totalAmount + activeReconciliation.total,
                caValidation = { it ?: BigDecimal.ZERO >= BigDecimal.ZERO },
                defaultAmountValidation = { (it ?: BigDecimal.ZERO).easyEquals(BigDecimal.ZERO) }
            )
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)
}