package com.tminus1010.buva.app

import com.tminus1010.buva.domain.ReconciliationStrategyGroup
import com.tminus1010.buva.domain.ResetStrategy
import com.tminus1010.buva.domain.Validate
import com.tminus1010.buva.domain.isFailure
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class IsReadyToReconcile @Inject constructor(
    private val userCategories: UserCategories,
) {
    class PlanIsInvalidException : Exception()

    suspend fun check() {
        if (
            userCategories.flow.first().any { category ->
                category.reconciliationStrategyGroup is ReconciliationStrategyGroup.Reservoir
                        && (category.reconciliationStrategyGroup.resetStrategy as? ResetStrategy.Basic)?.let { Validate.resetMax(it.budgetedMax).isFailure } ?: false
            }
        )
            throw PlanIsInvalidException()
    }
}

suspend fun IsReadyToReconcile.get() = runCatching { check(); true }.getOrDefault(false)