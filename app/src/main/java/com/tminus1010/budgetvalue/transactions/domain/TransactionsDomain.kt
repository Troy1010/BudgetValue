package com.tminus1010.budgetvalue.transactions.domain

import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._shared.date_period_getter.DatePeriodGetter
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay.ReplayDomain
import com.tminus1010.budgetvalue.replay.data.FutureRepo
import com.tminus1010.budgetvalue.replay.models.IFuture
import com.tminus1010.budgetvalue.replay.models.IReplayOrFuture
import com.tminus1010.budgetvalue.transactions.TransactionParser
import com.tminus1010.budgetvalue.transactions.data.TransactionsRepo
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.budgetvalue.transactions.models.TransactionsBlock
import com.tminus1010.tmcommonkotlin.rx.extensions.toSingle
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.Singles
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.InputStream
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionsDomain @Inject constructor(
    private val transactionsRepo: TransactionsRepo,
    private val datePeriodGetter: DatePeriodGetter,
    private val transactionParser: TransactionParser,
    private val replayDomain: ReplayDomain,
    private val futureRepo: FutureRepo,
) {
    // # Input
    fun importTransactions(inputStream: InputStream): Completable =
        Singles.zip(
            Single.fromCallable { transactionParser.parseToTransactions(inputStream) },
            replayDomain.autoReplays.toSingle(),
            futureRepo.fetchFutures().toSingle(),
        ).subscribeOn(Schedulers.io()).flatMapCompletable { (transactions, autoReplays, futures) ->
            Rx.merge(
                transactions.map { transaction ->
                    val futureOrReplay = (futures.find { it.predicate(transaction) } ?: autoReplays.find { it.predicate(transaction) })
                    if (futureOrReplay == null)
                        transactionsRepo.push(transaction)
                    else
                        transactionsRepo.push(futureOrReplay.categorize(transaction))
                            .run { if (futureOrReplay is IFuture && !futureOrReplay.isPermanent) andThen(futureRepo.delete(futureOrReplay.name)) else this }
                            .onErrorComplete() // error occurs when transaction already exists
                }
            )
        }

    fun applyReplayOrFutureToUncategorizedSpends(replay: IReplayOrFuture): Completable =
        uncategorizedSpends.toSingle()
            .flatMapCompletable { transactions ->
                Rx.merge(
                    transactions
                        .filter { replay.predicate(it) }
                        .map { transactionsRepo.update(replay.categorize(it)) }
                )
            }

    // # Internal
    private fun getBlocksFromTransactions(transactions: List<Transaction>): List<TransactionsBlock> {
        val transactionsRedefined = transactions.sortedBy { it.date }.toMutableList()
        val returning = ArrayList<TransactionsBlock>()
        if (0 !in transactionsRedefined.indices) return returning
        var datePeriod = datePeriodGetter.getDatePeriod(transactionsRedefined[0].date)
        while (datePeriod.startDate <= transactionsRedefined.last().date) {
            val transactionSet = transactionsRedefined
                .filter { it.date in datePeriod }
            transactionsRedefined.removeIf { it.date in datePeriod }
            if (transactionSet.isNotEmpty())
                returning += transactionSet
                    .fold(Pair(BigDecimal.ZERO, hashMapOf<Category, BigDecimal>())) { acc, transaction ->
                        transaction.categoryAmounts.forEach { acc.second[it.key] = it.value + (acc.second[it.key] ?: BigDecimal.ZERO) }
                        Pair(acc.first + transaction.amount, acc.second)
                    }
                    .let { TransactionsBlock(datePeriod, it.first, it.second) }
            if (transactionsRedefined.isEmpty()) break
            datePeriod = datePeriodGetter.getDatePeriod(transactionsRedefined[0].date)
        }
        return returning
    }

    // # Output
    val transactions = transactionsRepo.transactions
    val transactionBlocks: Observable<List<TransactionsBlock>> =
        transactions
            .map(::getBlocksFromTransactions)
    private val spends: Observable<List<Transaction>> =
        transactions
            .map { it.filter { it.isSpend } }
            .replay(1).refCount()
    val currentSpendBlockCAs: Observable<Map<Category, BigDecimal>> =
        spends
            .map {
                it
                    .filter { it.date in datePeriodGetter.getDatePeriod(LocalDate.now()) }
                    .map { it.categoryAmounts }
                    .fold(mapOf()) { acc, v ->
                        mutableSetOf<Category>().apply { addAll(acc.keys); addAll(v.keys) }
                            .associateWith { (acc[it] ?: BigDecimal.ZERO) + (v[it] ?: BigDecimal.ZERO) }
                    }
            }
    val uncategorizedSpends: Observable<List<Transaction>> =
        spends
            .map { it.filter { it.isUncategorized } }
    val firstUncategorizedSpend: Observable<Box<Transaction?>> =
        uncategorizedSpends
            .map { Box(it.getOrNull(0)) }
            .replay(1).refCount()
}