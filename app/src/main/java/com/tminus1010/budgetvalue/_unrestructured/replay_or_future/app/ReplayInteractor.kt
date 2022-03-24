package com.tminus1010.budgetvalue._unrestructured.replay_or_future.app

import com.tminus1010.budgetvalue._unrestructured.replay_or_future.domain.IReplay
import com.tminus1010.budgetvalue._unrestructured.transactions.app.Transaction
import com.tminus1010.budgetvalue._unrestructured.transactions.app.interactor.SaveTransactionInteractor
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReplayInteractor @Inject constructor(
    private val saveTransactionInteractor: SaveTransactionInteractor,
) {
    fun useReplayOnTransaction(replay: IReplay, transaction: Transaction): Completable {
        return saveTransactionInteractor.saveTransaction(replay.categorize(transaction))
    }
}