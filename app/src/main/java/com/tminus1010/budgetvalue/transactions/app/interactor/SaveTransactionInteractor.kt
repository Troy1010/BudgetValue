package com.tminus1010.budgetvalue.transactions.app.interactor

import com.tminus1010.budgetvalue._core.framework.source_objects.SourceArrayList
import com.tminus1010.budgetvalue._core.app.Redoable
import com.tminus1010.budgetvalue.transactions.data.repo.TransactionsRepo
import com.tminus1010.budgetvalue.transactions.app.Transaction
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
    private val undoQueue = SourceArrayList<Redoable>()
    private val redoQueue = SourceArrayList<Redoable>()

    // # Output
    val isUndoAvailable: Observable<Boolean> = undoQueue.observable
        .map { it.isNotEmpty() }
    val isRedoAvailable: Observable<Boolean> = redoQueue.observable
        .map { it.isNotEmpty() }
}