package com.tminus1010.budgetvalue.transactions.domain

import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceArrayList
import com.tminus1010.budgetvalue._core.models.Redoable
import com.tminus1010.budgetvalue.transactions.data.ITransactionsRepo
import com.tminus1010.budgetvalue.transactions.models.Transaction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategorizeTransactionsDomain @Inject constructor(
    private val transactionsRepo: ITransactionsRepo,
) {
    // # Input
    fun submitCategorization(transaction: Transaction): Completable {
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