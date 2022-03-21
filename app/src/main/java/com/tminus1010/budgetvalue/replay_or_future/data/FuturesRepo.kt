package com.tminus1010.budgetvalue.replay_or_future.data

import com.tminus1010.budgetvalue.all_features.data.MiscDAO
import com.tminus1010.budgetvalue.replay_or_future.domain.BasicFuture
import com.tminus1010.budgetvalue.replay_or_future.domain.IFuture
import com.tminus1010.budgetvalue.replay_or_future.domain.TerminationStrategy
import com.tminus1010.budgetvalue.replay_or_future.domain.TotalFuture
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class FuturesRepo @Inject constructor(
    private val miscDAO: MiscDAO,
) {
    suspend fun push(future: IFuture) {
        when (future) {
            is BasicFuture -> miscDAO.push(future)
            is TotalFuture -> miscDAO.push(future)
            else -> error("unhandled IFuture")
        }
    }

    suspend fun setTerminationStatus(future: IFuture, terminationStrategy: TerminationStrategy) {
        when (future) {
            is BasicFuture -> miscDAO.push(future.copy(terminationStrategy = terminationStrategy))
            is TotalFuture -> miscDAO.push(future.copy(terminationStrategy = terminationStrategy))
            else -> error("unhandled IFuture")
        }
    }

    suspend fun delete(future: IFuture) {
        when (future) {
            is BasicFuture -> miscDAO.deleteBasicFuture(future.name)
            is TotalFuture -> miscDAO.deleteTotalFuture(future.name)
            else -> error("unhandled IFuture")
        }
    }

    fun fetchFutures(): Flow<List<IFuture>> =
        combine(
            miscDAO.fetchBasicFutures(),
            miscDAO.fetchTotalFutures(),
        ) { a, b -> a + b }
}