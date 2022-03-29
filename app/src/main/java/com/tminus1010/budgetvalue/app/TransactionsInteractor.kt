package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue.domain.Transaction
import com.tminus1010.budgetvalue._unrestructured.transactions.app.TransactionBlock
import com.tminus1010.budgetvalue.data.TransactionsRepo
import com.tminus1010.budgetvalue.all_layers.extensions.value
import com.tminus1010.budgetvalue.app.model.ImportTransactionsResult
import com.tminus1010.budgetvalue.app.model.RedoUndo
import com.tminus1010.budgetvalue.data.FuturesRepo
import com.tminus1010.budgetvalue.data.service.TransactionInputStreamAdapter
import com.tminus1010.budgetvalue.domain.TerminationStrategy
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
    private val transactionInputStreamAdapter: TransactionInputStreamAdapter,
    private val futuresRepo: FuturesRepo,
    private val redoUndoInteractor: RedoUndoInteractor,
) {
    // # Input
    suspend fun importTransactions(inputStream: InputStream) = importTransactions(transactionInputStreamAdapter.parseToTransactions(inputStream))
    suspend fun importTransactions(transactions: Iterable<Transaction>): ImportTransactionsResult {
        var transactionsImportedCounter: Int
        var transactionsCategorizedCounter = 0
        var transactionsIgnoredBecauseTheyWereAlreadyImportedCounter = 0
        transactions
            .filter { (transactionsRepo.getTransaction2(it.id) == null).also { if (!it) transactionsIgnoredBecauseTheyWereAlreadyImportedCounter++ } }
            .map { transaction ->
                val matchedFuture =
                    futuresRepo.futures.value!!
                        .find { it.onImportMatcher?.isMatch(transaction) ?: false }
                matchedFuture?.categorize(transaction)
                    ?.also { if (matchedFuture.terminationStrategy == TerminationStrategy.ONCE) futuresRepo.setTerminationDate(matchedFuture, LocalDate.now()) }
                    ?.also { transactionsCategorizedCounter++ }
                    ?: transaction
            }
            .also { push(it.also { transactionsImportedCounter = it.size }) }
        return ImportTransactionsResult(
            numberOfTransactionsImported = transactionsImportedCounter,
            numberOfTransactionsCategorizedByFutures = transactionsCategorizedCounter,
            numberOfTransactionsIgnoredBecauseTheyWereAlreadyImported = transactionsIgnoredBecauseTheyWereAlreadyImportedCounter
        )
    }


    suspend fun push(vararg transactions: Transaction) = push(transactions.toList())
    suspend fun push(transactions: List<Transaction>) {
        val oldTransactionsAndIDs = transactions.map { Pair(transactionsRepo.getTransaction2(it.id), it.id) }
        redoUndoInteractor.useAndAdd(
            RedoUndo(
                redo = { transactions.forEach { transactionsRepo.push(it) } },
                undo = { oldTransactionsAndIDs.forEach { val (oldTransaction, id) = it; oldTransaction?.also { transactionsRepo.push(it) } ?: transactionsRepo.delete(id) } },
            )
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
    val transactionBlocks2 =
        transactionsRepo.transactionsAggregate
            .map { getBlocksFromTransactions(it.transactions) }
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)
    val spendBlocks =
        transactionBlocks2
            .map { it.map { it.spendBlock } }
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)
    val uncategorizedSpends =
        transactionsRepo.transactionsAggregate
            .map { it.spends.filter { it.isUncategorized } }
            .stateIn(GlobalScope, SharingStarted.Eagerly, emptyList())
    val mostRecentUncategorizedSpend =
        transactionsRepo.transactionsAggregate
            .map { it.mostRecentUncategorizedSpend }
            .stateIn(GlobalScope, SharingStarted.Eagerly, null)
}