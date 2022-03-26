package com.tminus1010.budgetvalue.data

import com.tminus1010.budgetvalue.data.service.MiscDAO
import com.tminus1010.budgetvalue.domain.Future
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class FuturesRepo @Inject constructor(
    private val miscDAO: MiscDAO,
) {
    suspend fun push(future: Future) {
        miscDAO.push(future)
    }

    suspend fun setTerminationDate(future: Future, terminationDate: LocalDate) {
        miscDAO.push(future.copy(terminationDate = terminationDate))
    }

    suspend fun delete(future: Future) {
        miscDAO.deleteFuture(future.name)
    }

    val futures: Flow<List<Future>> =
        miscDAO.fetchFutures()
}