package com.tminus1010.budgetvalue.transactions.app.use_case

import com.tminus1010.budgetvalue._core.framework.Rx
import com.tminus1010.budgetvalue.replay_or_future.domain.IReplayOrFuture
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.budgetvalue.transactions.data.repo.TransactionsRepo
import com.tminus1010.tmcommonkotlin.rx.extensions.toSingle
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class CategorizeAllMatchingUncategorizedTransactions @Inject constructor(
    private val transactionsInteractor: TransactionsInteractor,
    private val transactionsRepo: TransactionsRepo
) {
    /**
     * Emits an [Int] representing how many transactions were categorized
     */
    operator fun invoke(replay: IReplayOrFuture): Single<Int> {
        var counter = 0
        return transactionsInteractor.uncategorizedSpends.toSingle()
            .flatMapCompletable { transactions ->
                Rx.merge(
                    transactions
                        .filter { replay.predicate(it) }
                        .map { transactionsRepo.update(replay.categorize(it)).doOnComplete { counter++ } }
                )
            }
            .toSingle { counter }
    }
}