package com.tminus1010.budgetvalue.transactions.app.interactor

import com.tminus1010.budgetvalue._core.all.extensions.mapBox
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.app.DatePeriodService
import com.tminus1010.budgetvalue.importZ.data.LatestDateOfMostRecentImport
import com.tminus1010.budgetvalue.transactions.app.TransactionBlock
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay_or_future.data.FuturesRepo
import com.tminus1010.budgetvalue.replay_or_future.models.IReplayOrFuture
import com.tminus1010.budgetvalue.replay_or_future.models.TerminationStatus
import com.tminus1010.budgetvalue.transactions.app.TransactionsAggregate
import com.tminus1010.budgetvalue.transactions.data.TransactionParser
import com.tminus1010.budgetvalue.transactions.data.repo.TransactionsRepo
import com.tminus1010.budgetvalue.transactions.app.Transaction
import com.tminus1010.tmcommonkotlin.rx.extensions.toSingle
import com.tminus1010.tmcommonkotlin.rx.nonLazy
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.io.InputStream
import java.math.BigDecimal
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionsInteractor @Inject constructor(
    private val transactionsRepo: TransactionsRepo,
    private val datePeriodService: DatePeriodService,
    private val transactionParser: TransactionParser,
    private val futuresRepo: FuturesRepo,
    private val latestDateOfMostRecentImport: LatestDateOfMostRecentImport
) {
    // # Input
    fun importTransactions(inputStream: InputStream): Completable =
        importTransactions(transactionParser.parseToTransactions(inputStream))

    fun importTransactions(transactions: List<Transaction>): Completable {
        return futuresRepo.fetchFutures().toSingle().map { futures ->
            Rx.merge(
                transactions.map { transaction ->
                    (futures.find { it.predicate(transaction) }
                        ?.let { future ->
                            transactionsRepo.push(future.categorize(transaction))
                                .andThen(
                                    if (future.terminationStatus == TerminationStatus.WAITING_FOR_MATCH)
                                        futuresRepo.setTerminationStatus(future, TerminationStatus.TERMINATED(LocalDate.now()))
                                    else
                                        Completable.complete()
                                )
                        }
                        ?: transactionsRepo.push(transaction))
                        .onErrorComplete() // error occurs when transaction already exists
                }
            )
        }
            .flatMapCompletable { it }
            .andThen(
                Completable.fromAction { latestDateOfMostRecentImport.set(transactions.sortedBy { it.date }.last().date) }
                    .delay(2, TimeUnit.SECONDS) // TODO("Duct-tape solution to the fact that the previous completable completes before the repo emits, which ruins IsReconciliationReady")
            )
    }

    fun applyReplayOrFutureToUncategorizedSpends(replay: IReplayOrFuture): Single<Int> {
        var counter = 0
        return uncategorizedSpends.toSingle()
            .flatMapCompletable { transactions ->
                Rx.merge(
                    transactions
                        .filter { replay.predicate(it) }
                        .map { transactionsRepo.update(replay.categorize(it)).doOnComplete { counter++ } }
                )
            }
            .toSingle { counter }
    }

    // # Internal
    private fun getBlocksFromTransactions(transactions: List<Transaction>): List<TransactionBlock> {
        val transactionsRedefined = transactions.sortedBy { it.date }.toMutableList()
        val returning = ArrayList<TransactionBlock>()
        if (0 !in transactionsRedefined.indices) return returning
        var datePeriod = datePeriodService.getDatePeriod(transactionsRedefined[0].date)
        while (datePeriod.startDate <= transactionsRedefined.last().date) {
            val transactionSet = transactionsRedefined
                .filter { it.date in datePeriod }
            transactionsRedefined.removeIf { it.date in datePeriod }
            if (transactionSet.isNotEmpty())
                returning += TransactionBlock(transactionSet, datePeriod)
            if (transactionsRedefined.isEmpty()) break
            datePeriod = datePeriodService.getDatePeriod(transactionsRedefined[0].date)
        }
        return returning
    }

    // # Output
    val transactionBlocks: Observable<List<TransactionBlock>> =
        transactionsRepo.transactionsAggregate
            .map(TransactionsAggregate::transactions)
            .map(::getBlocksFromTransactions)
    val spendBlocks: Observable<List<TransactionBlock>> =
        transactionBlocks
            .map { it.map { it.spendBlock } }
    private val spends: Observable<List<Transaction>> =
        transactionsRepo.transactionsAggregate
            .map(TransactionsAggregate::spends)
            .replay(1).refCount()
    val currentSpendBlockCAs: Observable<Map<Category, BigDecimal>> =
        spends
            .map {
                it
                    .filter { it.date in datePeriodService.getDatePeriod(LocalDate.now()) }
                    .map { it.categoryAmounts }
                    .fold(mapOf()) { acc, v ->
                        mutableSetOf<Category>().apply { addAll(acc.keys); addAll(v.keys) }
                            .associateWith { (acc[it] ?: BigDecimal.ZERO) + (v[it] ?: BigDecimal.ZERO) }
                    }
            }
    val uncategorizedSpends: Observable<List<Transaction>> =
        spends
            .map { it.filter { it.isUncategorized } }
    val mostRecentUncategorizedSpend =
        transactionsRepo.transactionsAggregate
            .mapBox(TransactionsAggregate::mostRecentUncategorizedSpend)
            .replayNonError(1)
            .nonLazy()
}