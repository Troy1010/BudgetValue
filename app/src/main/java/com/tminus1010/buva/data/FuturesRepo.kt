package com.tminus1010.buva.data

import com.tminus1010.buva.all_layers.extensions.redoWhen
import com.tminus1010.buva.domain.Future
import com.tminus1010.buva.environment.adapter.MoshiWithCategoriesProvider
import com.tminus1010.buva.environment.database_or_datastore_or_similar.MiscDAO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import java.time.LocalDate
import javax.inject.Inject

class FuturesRepo @Inject constructor(
    private val moshiWithCategoriesProvider: MoshiWithCategoriesProvider,
    private val miscDAO: MiscDAO,
) {
    val futures =
        miscDAO.fetchFutures()
            .redoWhen(moshiWithCategoriesProvider.moshiFlow) // Room synchronously depends on moshiWithCategories, so we must redo when it emits.
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    suspend fun push(future: Future) =
        miscDAO.push(future)

    suspend fun setTerminationDate(future: Future, terminationDate: LocalDate) =
        miscDAO.push(future.copy(terminationDate = terminationDate))

    suspend fun delete(future: Future) =
        miscDAO.deleteFuture(future.name)
}