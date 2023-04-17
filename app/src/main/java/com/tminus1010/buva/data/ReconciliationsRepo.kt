package com.tminus1010.buva.data

import com.tminus1010.buva.all_layers.extensions.redoWhen
import com.tminus1010.buva.domain.Reconciliation
import com.tminus1010.buva.environment.adapter.MoshiWithCategoriesProvider
import com.tminus1010.buva.environment.database_or_datastore_or_similar.MiscDAO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReconciliationsRepo @Inject constructor(
    private val moshiWithCategoriesProvider: MoshiWithCategoriesProvider,
    private val miscDAO: MiscDAO,
) {
    val reconciliations =
        miscDAO.fetchReconciliations()
            .redoWhen(moshiWithCategoriesProvider.moshiFlow) // Room synchronously depends on moshiWithCategories, so we must redo when it emits.
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    suspend fun push(reconciliation: Reconciliation) =
        miscDAO.push(reconciliation)

    suspend fun delete(reconciliation: Reconciliation) =
        miscDAO.delete(reconciliation)
}