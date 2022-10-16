package com.tminus1010.buva.data

import com.tminus1010.buva.domain.Future
import com.tminus1010.buva.environment.MiscDAO
import com.tminus1010.buva.environment.UserCategoriesDAO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import java.time.LocalDate
import javax.inject.Inject

class FuturesRepo @Inject constructor(
    private val userCategoriesDAO: UserCategoriesDAO,
    private val miscDAO: MiscDAO,
) {
    val futures =
        userCategoriesDAO.fetchUserCategories().flatMapLatest {
            miscDAO.fetchFutures()
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    suspend fun push(future: Future) =
        miscDAO.push(future)

    suspend fun setTerminationDate(future: Future, terminationDate: LocalDate) =
        miscDAO.push(future.copy(terminationDate = terminationDate))

    suspend fun delete(future: Future) =
        miscDAO.deleteFuture(future.name)
}