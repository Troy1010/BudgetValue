package com.tminus1010.budgetvalue.transactions.app.interactor

import com.tminus1010.budgetvalue._core.domain.Redoable
import com.tminus1010.budgetvalue._core.framework.Rx
import com.tminus1010.budgetvalue._core.framework.source_objects.SourceList
import com.tminus1010.budgetvalue.transactions.app.Transaction
import com.tminus1010.budgetvalue.transactions.data.repo.TransactionsRepo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject
import javax.inject.Singleton

// TODO: undo/redo logic should be separate from saveTransaction, maybe?
@Singleton
class SaveTransactionInteractor @Inject constructor(
    private val transactionsRepo: TransactionsRepo,
) {
    // # Input
    fun saveTransaction(transaction: Transaction): Completable {
        return transactionsRepo.getTransaction(transaction.id)
            .flatMapCompletable { oldTransaction ->
                Redoable(
                    redo = transactionsRepo.update(transaction),
                    undo = transactionsRepo.update(oldTransaction.copy(id = transaction.id))
                ).let { it.redo.doOnComplete { undoQueue.add(it) } }
            }
    }

    suspend fun saveTransactions(transactions: List<Transaction>) {
        val oldTransactions = transactions.map { transactionsRepo.getTransaction2(it.id) } // TODO: Make sure error is throw if an old transaction can't be found, or implement its removal
        val redoable =
            Redoable(
                Rx.completableFromSuspend { transactions.forEach { transactionsRepo.update2(it) } },
                Rx.completableFromSuspend { oldTransactions.forEach { transactionsRepo.update2(it) } }
            )
        redoable.redo.doOnComplete { undoQueue.add(redoable) }.blockingAwait()
    }

    fun undo(): Completable {
        return undoQueue.takeLast()
            ?.let { it.undo.doOnComplete { redoQueue.add(it) } }
            ?: Completable.complete()
    }

    fun redo(): Completable {
        return redoQueue.takeLast()
            ?.let { it.redo.doOnComplete { undoQueue.add(it) } }
            ?: Completable.complete()
    }

    // # Internal
    private val undoQueue = SourceList<Redoable>()
    private val redoQueue = SourceList<Redoable>()

    // # Output
    val isUndoAvailable: Observable<Boolean> = undoQueue.observable
        .map { it.isNotEmpty() }
    val isRedoAvailable: Observable<Boolean> = redoQueue.observable
        .map { it.isNotEmpty() }
}