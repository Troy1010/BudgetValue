package com.tminus1010.budgetvalue._unrestructured.reconcile.data

import com.tminus1010.budgetvalue._unrestructured.reconcile.domain.Reconciliation
import com.tminus1010.budgetvalue.data.service.MiscDAO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReconciliationsRepo @Inject constructor(
    private val miscDAO: MiscDAO,
) {
    suspend fun push(reconciliation: Reconciliation) =
        miscDAO.push(reconciliation)

    suspend fun delete(reconciliation: Reconciliation) =
        miscDAO.delete(reconciliation)

    val reconciliations =
        miscDAO.fetchReconciliations()
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)
}