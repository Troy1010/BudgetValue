package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue._unrestructured.transactions.app.Transaction
import com.tminus1010.budgetvalue._unrestructured.transactions.data.repo.TransactionsRepo
import com.tminus1010.budgetvalue.app.model.RedoUndo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveTransactionInteractor @Inject constructor(
    private val transactionsRepo: TransactionsRepo,
    private val redoUndoInteractor: RedoUndoInteractor,
) {
    // # Input
    suspend fun saveTransactions(vararg transactions: Transaction) = saveTransactions(transactions.toList())
    suspend fun saveTransactions(transactions: List<Transaction>) {
        val oldTransactionsAndIDs = transactions.map { Pair(transactionsRepo.getTransaction2(it.id), it.id) }
        redoUndoInteractor.useAndAdd(
            RedoUndo(
                redo = { transactions.forEach { transactionsRepo.push(it) } },
                undo = { oldTransactionsAndIDs.forEach { val (oldTransaction, id) = it; oldTransaction?.also { transactionsRepo.push(it) } ?: transactionsRepo.delete(id) } },
            )
        )
    }
}