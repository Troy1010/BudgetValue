package com.tminus1010.budgetvalue.auto_replay

import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue.auto_replay.data.IAutoReplayRepo
import com.tminus1010.budgetvalue.auto_replay.models.AutoReplay
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.data.ITransactionsRepo
import com.tminus1010.tmcommonkotlin.rx.extensions.doLogx
import com.tminus1010.tmcommonkotlin.rx.extensions.toSingle
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.Singles
import java.math.BigDecimal
import javax.inject.Inject

class AutoReplayDomain @Inject constructor(
    private val autoReplayRepo: IAutoReplayRepo,
    private val transactionsRepo: ITransactionsRepo,
) {
    // # Input
    fun addAutoReplay(description: String, categoryAmounts: Map<Category, BigDecimal>): Completable =
        autoReplayRepo.add(AutoReplay(description, categoryAmounts))
            .andThen(
                Singles.zip(
                    autoReplays.toSingle(),
                    transactionsRepo.transactions.toSingle(),
                )
                    .flatMapCompletable { (autoReplays, transactions) ->
                        Rx.merge(
                            transactions
                                .filter { autoReplays[it.description] != null }
                                .map { transactionsRepo.update(it.copy(categoryAmounts = autoReplays[it.description]!!)) }
                        )
                    }
            )

    // # Output
    val autoReplays: Observable<Map<String, Map<Category, BigDecimal>>> =
        autoReplayRepo.fetchAutoReplays()
            .map { it.associate { it.description to it.categoryAmounts } }
            .replay(1).refCount()
}