package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue._unrestructured.transactions.app.Transaction
import com.tminus1010.budgetvalue._unrestructured.transactions.app.TransactionBlock
import com.tminus1010.budgetvalue._unrestructured.transactions.app.TransactionsAggregate
import com.tminus1010.budgetvalue._unrestructured.transactions.data.TransactionAdapter
import com.tminus1010.budgetvalue._unrestructured.transactions.data.repo.TransactionsRepo
import com.tminus1010.budgetvalue.all_layers.extensions.value
import com.tminus1010.budgetvalue.app.model.ImportTransactionsResult
import com.tminus1010.budgetvalue.data.FuturesRepo
import com.tminus1010.budgetvalue.data.LatestDateOfMostRecentImportRepo
import com.tminus1010.budgetvalue.domain.DatePeriodService
import com.tminus1010.budgetvalue.domain.TerminationStrategy
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import java.io.InputStream
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionsInteractor @Inject constructor(
    private val transactionsRepo: TransactionsRepo,
    private val datePeriodService: DatePeriodService,
    private val transactionAdapter: TransactionAdapter,
    private val futuresRepo: FuturesRepo,
    private val latestDateOfMostRecentImportRepo: LatestDateOfMostRecentImportRepo,
) {
    // # Input
    suspend fun importTransactions(transactions: List<Transaction>): ImportTransactionsResult {
        var categorizedCounter = 0
        var transactionsImportedCounter = 0
        var transactionsIgnoredBecauseTheyWereAlreadyImported = 0
        transactions
            .filter { (transactionsRepo.getTransaction2(it.id) == null).also { if (!it) transactionsIgnoredBecauseTheyWereAlreadyImported++ } }
            .forEach { transaction ->
                val matchedFuture =
                    futuresRepo.futures.value!!
                        .find { it.onImportMatcher.isMatch(transaction) }
                val transactionToPush = matchedFuture?.categorize(transaction)?.also { categorizedCounter++ } ?: transaction
                transactionsRepo.push(transactionToPush).blockingAwait()
                if (matchedFuture != null && matchedFuture.terminationStrategy == TerminationStrategy.ONCE)
                    futuresRepo.setTerminationDate(matchedFuture, LocalDate.now())
                transactionsImportedCounter++
            }
        // TODO: Make sure that IsReconciliationReady works with this change
        transactions.maxByOrNull { it.date }
            ?.also { mostRecentTransaction -> latestDateOfMostRecentImportRepo.set(mostRecentTransaction.date) }
        //
        return ImportTransactionsResult(
            numberOfTransactionsCategorizedByFutures = categorizedCounter,
            numberOfTransactionsImported = transactionsImportedCounter,
            numberOfTransactionsIgnoredBecauseTheyWereAlreadyImported = transactionsIgnoredBecauseTheyWereAlreadyImported
        )
    }

    suspend fun importTransactions(inputStream: InputStream) =
        importTransactions(transactionAdapter.parseToTransactions(inputStream))

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
    val spendBlocks =
        transactionBlocks2
            .map { it.map { it.spendBlock } }
    private val spends =
        transactionsRepo.transactionsAggregate2
            .map { it.spends }
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed())
    val uncategorizedSpends =
        spends
            .map { it.filter { it.isUncategorized } }
            .stateIn(GlobalScope, SharingStarted.Eagerly, emptyList())
    val mostRecentUncategorizedSpend =
        transactionsRepo.transactionsAggregate2
            .map { it.mostRecentUncategorizedSpend }
            .stateIn(GlobalScope, SharingStarted.Eagerly, null)
}