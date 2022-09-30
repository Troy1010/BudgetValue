package com.tminus1010.buva.app

import com.tminus1010.buva.data.ActiveReconciliationRepo
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MatchBudgetedForActiveReconciliation @Inject constructor(
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val budgetedForActiveReconciliationInteractor: BudgetedForActiveReconciliationInteractor,
) {
    suspend operator fun invoke() {
        activeReconciliationRepo.activeReconciliationCAs.first()
            .subtractTogether(budgetedForActiveReconciliationInteractor.categoryAmountsAndTotal.first().categoryAmounts)
            .also { activeReconciliationRepo.pushCategoryAmounts(it) }
    }
}