package com.tminus1010.buva.app

import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.domain.Category
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
import javax.inject.Inject

/**
 * This secondary ActiveReconciliationInteractor is here to avoid circular dependencies
 */
class ActiveReconciliationInteractor2 @Inject constructor(
    private val budgetedWithActiveReconciliationInteractor: BudgetedWithActiveReconciliationInteractor,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
) {
    suspend fun dumpIntoCategory(category: Category) {
        budgetedWithActiveReconciliationInteractor.categoryAmountsAndTotal.first().defaultAmount
            .plus(activeReconciliationRepo.activeReconciliationCAs.first()[category] ?: BigDecimal.ZERO)
            .also { activeReconciliationRepo.pushCategoryAmount(category, it) }
    }
}