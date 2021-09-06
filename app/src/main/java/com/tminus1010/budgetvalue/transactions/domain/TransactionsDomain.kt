package com.tminus1010.budgetvalue.transactions.domain

import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._shared.date_period_getter.DatePeriodGetter
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay_or_future.data.FuturesRepo
import com.tminus1010.budgetvalue.replay_or_future.models.IReplayOrFuture
import com.tminus1010.budgetvalue.replay_or_future.models.TerminationStatus
import com.tminus1010.budgetvalue.transactions.TransactionParser
import com.tminus1010.budgetvalue.transactions.data.TransactionsRepo
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.budgetvalue.transactions.models.TransactionBlock
import com.tminus1010.tmcommonkotlin.rx.extensions.toSingle
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
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
    private val futuresRepo: FuturesRepo,
) {
    // # Input
    fun importTransactions(inputStream: InputStream): Completable =
        importTransactions(transactionParser.parseToTransactions(inputStream))

    fun importTransactions(transactions: List<Transaction>): Completable {
        return futuresRepo.fetchFutures().toSingle().map { futures ->
            Rx.merge(
                transactions.map { transaction ->
                    futures.find { it.predicate(transaction) }
                        ?.let { future ->
                            transactionsRepo.push(future.categorize(transaction))
                                .andThen(
                                    if (future.terminationStatus == TerminationStatus.WAITING_FOR_MATCH)
                                        futuresRepo.setTerminationStatus(future, TerminationStatus.TERMINATED(LocalDate.now()))
                                    else
                                        Completable.complete()
                                )
                                .onErrorComplete() // error occurs when transaction already exists
                        }
                        ?: transactionsRepo.push(transaction)
                }
            )
        }
            .flatMapCompletable { it }
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
    private fun getBlocksFromTransactions(transactions: List<Transaction>): List<TransactionBlock> {
        val transactionsRedefined = transactions.sortedBy { it.date }.toMutableList()
        val returning = ArrayList<TransactionBlock>()
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
                    .let { TransactionBlock(datePeriod, it.first, it.second) }
            if (transactionsRedefined.isEmpty()) break
            datePeriod = datePeriodGetter.getDatePeriod(transactionsRedefined[0].date)
        }
        return returning
    }

    // # Output
    val transactions = transactionsRepo.transactions
    val transactionBlocks: Observable<List<TransactionBlock>> =
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