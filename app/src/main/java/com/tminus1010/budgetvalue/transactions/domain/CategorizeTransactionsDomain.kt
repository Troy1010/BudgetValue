package com.tminus1010.budgetvalue.transactions.domain

import com.tminus1010.budgetvalue._core.extensions.copy
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceArrayList
import com.tminus1010.budgetvalue._core.models.Redoable
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.data.ITransactionsRepo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategorizeTransactionsDomain @Inject constructor(
    private val transactionsRepo: ITransactionsRepo,
) {
    // # Input
    fun submitCategorization(id: String, category: Category): Completable {
        return transactionsRepo.getTransaction(id)
            .flatMapCompletable { oldTransaction ->
                Redoable(
                    redo = oldTransaction
                        .let { transaction ->
                            transaction.categoryAmounts
                                .filter { it.key != category }
                                .let {
                                    it.copy(category to transaction.amount - it.map { it.value }
                                        .fold(0.toBigDecimal()) { acc, v -> acc + v })
                                }
                        }
                        .let { transactionsRepo.pushTransactionCAs(id, it) },
                    undo = transactionsRepo.pushTransactionCAs(
                        id,
                        oldTransaction.categoryAmounts
                    )
                ).let { it.redo.doOnComplete { undoQueue.add(it) } }
            }
    }
    fun submitCategorization(id: String, categoryAmounts: Map<Category, BigDecimal>): Completable {
        return transactionsRepo.getTransaction(id)
            .flatMapCompletable { oldTransaction ->
                Redoable(
                    redo = categoryAmounts
                        .let { transactionsRepo.pushTransactionCAs(id, it) },
                    undo = transactionsRepo.pushTransactionCAs(
                        id,
                        oldTransaction.categoryAmounts
                    )
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