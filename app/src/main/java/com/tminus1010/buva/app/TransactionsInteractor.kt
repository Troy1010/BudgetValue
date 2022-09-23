package com.tminus1010.buva.app

import com.tminus1010.buva.app.model.RedoUndo
import com.tminus1010.buva.data.TransactionsRepo
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.buva.domain.TransactionBlock
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionsInteractor @Inject constructor(
    private val transactionsRepo: TransactionsRepo,
    private val datePeriodService: DatePeriodService,
    private val redoUndoInteractor: RedoUndoInteractor,
) {
    // # Input
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

    suspend fun clear() {
        val oldTransactionsAndIDs = transactionsRepo.transactionsAggregate.first().transactions.map { Pair(transactionsRepo.getTransaction2(it.id), it.id) }
        redoUndoInteractor.useAndAdd(
            RedoUndo(
                redo = { transactionsRepo.clear() },
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
                returning += TransactionBlock.create(transactionSet, datePeriod)
            if (transactionsRedefined.isEmpty()) break
            datePeriod = datePeriodService.getDatePeriod(transactionsRedefined[0].date)
        }
        return returning
    }

    // # Output
    val transactionBlocks =
        transactionsRepo.transactionsAggregate
            .map { getBlocksFromTransactions(it.transactions) }
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)
    val incomeBlocks =
        transactionBlocks
            .map { it.map { it.incomeBlock } }
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)
    val spendBlocks =
        transactionBlocks
            .map { it.map { it.spendBlock } }
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)
    val uncategorizedSpends =
        transactionsRepo.transactionsAggregate
            .map { it.spends.filter { it.isUncategorized } }
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)
    val mostRecentUncategorizedSpend =
        transactionsRepo.transactionsAggregate
            .map { it.mostRecentUncategorizedSpend }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)
}