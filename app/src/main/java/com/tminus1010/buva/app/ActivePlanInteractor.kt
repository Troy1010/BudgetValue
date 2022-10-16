package com.tminus1010.buva.app

import com.tminus1010.buva.all_layers.extensions.isZero
import com.tminus1010.buva.data.ActivePlanRepo
import com.tminus1010.buva.domain.averagedCategoryAmounts
import com.tminus1010.buva.domain.averagedTotal
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ActivePlanInteractor @Inject constructor(
    private val activePlanRepo: ActivePlanRepo,
    private val transactionsInteractor: TransactionsInteractor,
) {
    val activePlan = activePlanRepo.activePlan

    suspend fun estimateActivePlanFromHistory() {
        activePlanRepo.updateTotal(transactionsInteractor.incomeBlocks.first().averagedTotal())
        activePlanRepo.pushCategoryAmounts(transactionsInteractor.spendBlocks.first().filter { it.defaultAmount.isZero }.averagedCategoryAmounts())
    }
}