package com.tminus1010.buva.app

import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.domain.Category
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * This secondary ActiveReconciliationInteractor is here to avoid circular dependencies
 */
class ActiveReconciliationInteractor2 @Inject constructor(
    private val activeReconciliationInteractor: ActiveReconciliationInteractor,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
) {
    suspend fun fillIntoCategory(category: Category) {
        Pair(activeReconciliationRepo.activeReconciliationCAs.first(), activeReconciliationInteractor.targetDefaultAmount.first())
            .let { (activeReconciliationCAs, targetDefaultAmount) ->
                activeReconciliationCAs.calcCategoryAmountToGetTargetDefaultAmount(category, targetDefaultAmount)
            }
            .also { activeReconciliationRepo.pushCategoryAmount(category, it) }
    }
}