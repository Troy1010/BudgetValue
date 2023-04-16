package com.tminus1010.buva.app

import com.tminus1010.buva.domain.ReconciliationToDo
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class IsReadyToBudgeted @Inject constructor(
    private val reconciliationsToDoInteractor: ReconciliationsToDoInteractor,
) {
    class ReconciliationRequiredException : Exception()

    suspend fun check() {
        if (reconciliationsToDoInteractor.currentReconciliationToDo.first() !is ReconciliationToDo.Anytime)
            throw ReconciliationRequiredException()
    }
}

suspend fun IsReadyToBudgeted.get() = runCatching { check(); true }.getOrDefault(false)