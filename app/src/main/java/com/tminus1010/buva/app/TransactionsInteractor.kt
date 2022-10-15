package com.tminus1010.buva.app

import com.tminus1010.buva.app.model.RedoUndo
import com.tminus1010.buva.data.TransactionsRepo
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.buva.domain.TransactionBlock
import com.tminus1010.buva.domain.TransactionsAggregate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionsInteractor @Inject constructor(
    private val transactionsRepo: TransactionsRepo,
    private val datePeriodService: DatePeriodService,
    private val undoService: UndoService,
    private val isPeriodFullyImported: IsPeriodFullyImported,
) {
    // # Input
    suspend fun push(vararg transactions: Transaction) = push(transactions.toList())
    suspend fun push(transactions: List<Transaction>) {
        val oldTransactionsAndIDs = transactions.map { Pair(transactionsRepo.getTransaction(it.id), it.id) }
        undoService.useAndAdd(
            RedoUndo(
                redo = { transactionsRepo.push(transactions) },
                undo = { oldTransactionsAndIDs.forEach { val (oldTransaction, id) = it; oldTransaction?.also { transactionsRepo.push(it) } ?: transactionsRepo.deleteTransaction(id) } },
            )
        )
    }

    suspend fun clear() {
        val oldTransactionsAndIDs = transactionsAggregate.first().transactions.map { Pair(transactionsRepo.getTransaction(it.id), it.id) }
        undoService.useAndAdd(
            RedoUndo(
                redo = { transactionsRepo.clearTransactions() },
                undo = { oldTransactionsAndIDs.forEach { val (oldTransaction, id) = it; oldTransaction?.also { transactionsRepo.push(it) } ?: transactionsRepo.deleteTransaction(id) } },
            )
        )
    }

    // # Output
    val transactionsAggregate =
        transactionsRepo.fetchTransactions()
            .map { TransactionsAggregate(it) }
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)
    val transactionBlocks =
        combine(transactionsAggregate, datePeriodService.getDatePeriodLambda, isPeriodFullyImported.isPeriodFullyImportedLambda)
        { transactionsAggregate, getDatePeriodLambda, isPeriodFullyImportedLambda ->
            transactionsAggregate.transactions
                .groupBy { getDatePeriodLambda(it.date) }
                .map { (datePeriod, transactions) ->
                    TransactionBlock(datePeriod, transactions, isPeriodFullyImportedLambda(datePeriod))
                }
        }
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
        transactionsAggregate
            .map { it.spends.filter { it.isUncategorized } }
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)
    val mostRecentUncategorizedSpend =
        transactionsAggregate
            .map { it.mostRecentUncategorizedSpend }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1) // TODO: Should try to not use SharingStarted.Eagerly
}