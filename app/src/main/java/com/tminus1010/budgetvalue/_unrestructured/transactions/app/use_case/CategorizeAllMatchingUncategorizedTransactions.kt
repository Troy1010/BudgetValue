package com.tminus1010.budgetvalue._unrestructured.transactions.app.use_case

import com.tminus1010.budgetvalue.framework.Rx
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.domain.IReplayOrFuture
import com.tminus1010.budgetvalue._unrestructured.transactions.app.Transaction
import com.tminus1010.budgetvalue._unrestructured.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.budgetvalue._unrestructured.transactions.data.repo.TransactionsRepo
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
    operator fun invoke(predicate: (Transaction) -> Boolean, categorization: (Transaction) -> Transaction): Single<Int> {
        var counter = 0
        return transactionsInteractor.uncategorizedSpends.toSingle()
            .flatMapCompletable { transactions ->
                Rx.merge(
                    transactions
                        .filter { predicate(it) }
                        .map { transactionsRepo.update(categorization(it)).doOnComplete { counter++ } }
                )
            }
            .toSingle { counter }
    }

    /**
     * This is a convenience overload.
     *
     * Emits an [Int] representing how many transactions were categorized
     */
    operator fun invoke(replay: IReplayOrFuture): Single<Int> {
        return invoke(replay::shouldCategorizeOnImport, replay::categorize)
    }
}