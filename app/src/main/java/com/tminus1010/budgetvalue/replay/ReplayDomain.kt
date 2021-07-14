package com.tminus1010.budgetvalue.replay

import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue.replay.data.ReplayRepo
import com.tminus1010.budgetvalue.replay.models.IReplay
import com.tminus1010.budgetvalue.replay.models.IReplayOrFuture
import com.tminus1010.budgetvalue.transactions.data.TransactionsRepo
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.toSingle
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReplayDomain @Inject constructor(
    replayRepo: ReplayRepo,
    private val transactionsRepo: TransactionsRepo,
    private val transactionsDomain: TransactionsDomain,
) {
    val autoReplays: Observable<List<IReplay>> =
        replayRepo.fetchReplays()
            .map { it.filter { it.isAutoReplay } }
            .replay(1).autoConnect()

    fun applyReplayOrFutureToUncategorizedSpends(replay: IReplayOrFuture): Completable =
        transactionsDomain.uncategorizedSpends.toSingle()
            .flatMapCompletable { transactions ->
                Rx.merge(
                    transactions
                        .filter { replay.predicate(it) }
                        .map { transactionsRepo.update(replay.categorize(it)) }
                )
            }
}