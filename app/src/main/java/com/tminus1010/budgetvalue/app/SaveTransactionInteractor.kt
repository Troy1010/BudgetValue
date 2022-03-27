package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue._unrestructured.transactions.app.Transaction
import com.tminus1010.budgetvalue._unrestructured.transactions.data.repo.TransactionsRepo
import com.tminus1010.budgetvalue.app.model.Redoable
import com.tminus1010.budgetvalue.framework.source_objects.SourceList
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// TODO: undo/redo logic should be separate from saveTransaction, maybe?
@Singleton
class SaveTransactionInteractor @Inject constructor(
    private val transactionsRepo: TransactionsRepo,
) {
    // # Input
    suspend fun saveTransaction(transaction: Transaction) {
        val oldTransactionAndID = Pair(transactionsRepo.getTransaction2(transaction.id), transaction.id)
        val redoable =
            Redoable(
                redo = { transactionsRepo.push(transaction) },
                undo = { val (oldTransaction, id) = oldTransactionAndID; oldTransaction?.also { transactionsRepo.push(it) } ?: transactionsRepo.delete(id) },
            )
        redoable.redo()
        undoQueue.add(redoable)
    }

    suspend fun saveTransactions(transactions: List<Transaction>) {
        val oldTransactionsAndIDs = transactions.map { Pair(transactionsRepo.getTransaction2(it.id), it.id) }
        val redoable =
            Redoable(
                redo = { transactions.forEach { transactionsRepo.push(it) } },
                undo = { oldTransactionsAndIDs.forEach { val (oldTransaction, id) = it; oldTransaction?.also { transactionsRepo.push(it) } ?: transactionsRepo.delete(id) } },
            )
        redoable.redo()
        undoQueue.add(redoable)
    }

    suspend fun undo() {
        undoQueue.takeLast()
            ?.also { redoQueue.add(it) }
            ?.also { it.undo() }
    }

    suspend fun redo() {
        redoQueue.takeLast()
            ?.also { undoQueue.add(it) }
            ?.also { it.redo() }
    }

    // # Internal
    private val undoQueue = SourceList<Redoable>()
    private val redoQueue = SourceList<Redoable>()

    // # Output
    val isUndoAvailable = undoQueue.flow.map { it.isNotEmpty() }
    val isRedoAvailable = redoQueue.flow.map { it.isNotEmpty() }
}