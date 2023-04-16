package com.tminus1010.buva.app

import com.tminus1010.buva.all_layers.extensions.isZero
import com.tminus1010.buva.domain.ReconciliationStrategyGroup
import com.tminus1010.buva.domain.ResetStrategy
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
                        && (category.reconciliationStrategyGroup.resetStrategy as? ResetStrategy.Basic)?.let { it.budgetedMax?.isZero ?: false } ?: false
            }
        )
            throw PlanIsInvalidException()
    }
}

suspend fun IsReadyToReconcile.get() = runCatching { check(); true }.getOrDefault(false)