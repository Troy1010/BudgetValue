package com.tminus1010.budgetvalue.transactions.app.interactor

import com.tminus1010.budgetvalue._core.all_layers.extensions.mapBox
import com.tminus1010.budgetvalue._core.domain.DatePeriodService
import com.tminus1010.budgetvalue._core.framework.Rx
import com.tminus1010.budgetvalue.importZ.data.LatestDateOfMostRecentImport
import com.tminus1010.budgetvalue.replay_or_future.data.FuturesRepo
import com.tminus1010.budgetvalue.replay_or_future.domain.TerminationStrategy
import com.tminus1010.budgetvalue.transactions.app.Transaction
import com.tminus1010.budgetvalue.transactions.app.TransactionBlock
import com.tminus1010.budgetvalue.transactions.app.TransactionsAggregate
import com.tminus1010.budgetvalue.transactions.data.TransactionAdapter
import com.tminus1010.budgetvalue.transactions.data.repo.TransactionsRepo
import com.tminus1010.tmcommonkotlin.rx.extensions.toSingle
import com.tminus1010.tmcommonkotlin.rx.nonLazy
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import java.io.InputStream
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionsInteractor @Inject constructor(
    private val transactionsRepo: TransactionsRepo,
    private val datePeriodService: DatePeriodService,
    private val transactionAdapter: TransactionAdapter,
    private val futuresRepo: FuturesRepo,
    private val latestDateOfMostRecentImport: LatestDateOfMostRecentImport
) {
    // # Input
    fun importTransactions(inputStream: InputStream): Completable =
        importTransactions(transactionAdapter.parseToTransactions(inputStream))

    fun importTransactions(transactions: List<Transaction>): Completable {
        return futuresRepo.fetchFutures().toSingle().map { futures ->
            Rx.merge(
                transactions.map { transaction ->
                    (futures.find { it.shouldCategorizeOnImport(transaction) }
                        ?.let { future ->
                            transactionsRepo.push(future.categorize(transaction))
                                .andThen(
                                    if (future.terminationStrategy == TerminationStrategy.WAITING_FOR_MATCH)
                                        futuresRepo.setTerminationStatus(future, TerminationStrategy.TERMINATED(LocalDate.now()))
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
                Completable.fromAction { transactions.maxByOrNull { it.date }?.also { latestDateOfMostRecentImport.set(it.date) } }
                    .delay(2, TimeUnit.SECONDS) // TODO("Duct-tape solution to the fact that the previous completable completes before the repo emits, which ruins IsReconciliationReady")
            )
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
    @Deprecated("use transactionBlocks2")
    val transactionBlocks: Observable<List<TransactionBlock>> =
        transactionsRepo.transactionsAggregate
            .map(TransactionsAggregate::transactions)
            .map(::getBlocksFromTransactions)
    val transactionBlocks2 =
        transactionsRepo.transactionsAggregate2
            .map { getBlocksFromTransactions(it.transactions) }
    @Deprecated("use spendBlocks2")
    val spendBlocks: Observable<List<TransactionBlock>> =
        transactionBlocks
            .map { it.map { it.spendBlock } }
    val spendBlocks2 =
        transactionBlocks2
            .map { it.map { it.spendBlock } }
    @Deprecated("use spends2")
    private val spends: Observable<List<Transaction>> =
        transactionsRepo.transactionsAggregate
            .map(TransactionsAggregate::spends)
            .replay(1).refCount()
    private val spends2 =
        transactionsRepo.transactionsAggregate2
            .map { it.spends }
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed())
    @Deprecated("use uncategorizedSpends2")
    val uncategorizedSpends: Observable<List<Transaction>> =
        spends
            .map { it.filter { it.isUncategorized } }
    val uncategorizedSpends2 =
        spends2
            .map { it.filter { it.isUncategorized } }
            .stateIn(GlobalScope, SharingStarted.Eagerly, emptyList())
    @Deprecated("use mostRecentUncategorizedSpend2")
    val mostRecentUncategorizedSpend =
        transactionsRepo.transactionsAggregate
            .mapBox(TransactionsAggregate::mostRecentUncategorizedSpend)
            .replayNonError(1)
            .nonLazy()
    val mostRecentUncategorizedSpend2 =
        transactionsRepo.transactionsAggregate2
            .map { it.mostRecentUncategorizedSpend }
            .stateIn(GlobalScope, SharingStarted.Eagerly, null)
}