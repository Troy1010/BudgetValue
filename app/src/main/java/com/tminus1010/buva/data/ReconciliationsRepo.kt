package com.tminus1010.buva.data

import com.tminus1010.buva.domain.Reconciliation
import com.tminus1010.buva.environment.database_or_datastore_or_similar.MiscDAO
import com.tminus1010.buva.environment.database_or_datastore_or_similar.UserCategoriesDAO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReconciliationsRepo @Inject constructor(
    private val userCategoriesDAO: UserCategoriesDAO,
    private val miscDAO: MiscDAO,
) {
    val reconciliations =
        userCategoriesDAO.fetchUserCategories().flatMapLatest {
            miscDAO.fetchReconciliations()
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    suspend fun push(reconciliation: Reconciliation) =
        miscDAO.push(reconciliation)

    suspend fun delete(reconciliation: Reconciliation) =
        miscDAO.delete(reconciliation)
}