package com.tminus1010.buva.data

import com.tminus1010.buva.data.service.MiscDAO
import com.tminus1010.buva.domain.ReconciliationSkip
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReconciliationSkipRepo @Inject constructor(
    private val miscDAO: MiscDAO,
) {
    val reconciliationSkips =
        miscDAO.fetchReconciliationSkips()
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    suspend fun push(reconciliationSkip: ReconciliationSkip) =
        miscDAO.insert(reconciliationSkip)

    suspend fun delete(reconciliationSkip: ReconciliationSkip) =
        miscDAO.delete(reconciliationSkip)

    suspend fun delete(reconciliationSkips: List<ReconciliationSkip>) =
        miscDAO.delete(reconciliationSkips)
}