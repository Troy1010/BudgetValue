package com.tminus1010.buva.app

import com.tminus1010.buva.data.ActiveReconciliationRepo
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class EqualizeActiveReconciliation @Inject constructor(
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val budgetedWithActiveReconciliationInteractor: BudgetedWithActiveReconciliationInteractor,
) {
    suspend operator fun invoke() {
        activeReconciliationRepo.activeReconciliationCAs.first()
            .subtractTogether(budgetedWithActiveReconciliationInteractor.categoryAmountsAndTotal.first().categoryAmounts)
            .also { activeReconciliationRepo.pushCategoryAmounts(it) }
    }
}