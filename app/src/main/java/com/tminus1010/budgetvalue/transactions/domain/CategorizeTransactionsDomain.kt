package com.tminus1010.budgetvalue.transactions.domain

import com.tminus1010.budgetvalue._core.extensions.copy
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceArrayList
import com.tminus1010.budgetvalue._core.models.Redoable
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.data.ITransactionsRepo
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategorizeTransactionsDomain @Inject constructor(
    private val transactionsRepo: ITransactionsRepo,
) {
    // # Input
    fun submitCategorization(id: String, category: Category) {
        transactionsRepo.getTransaction(id)
            .subscribeBy(onSuccess = { oldTransaction ->
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
                )
                    .also { it.redo.subscribeBy(onComplete = { undoQueue.add(it) }) }
            })
    }
    fun submitCategorization(id: String, categoryAmounts: Map<Category, BigDecimal>) {
        transactionsRepo.getTransaction(id)
            .subscribeBy(onSuccess = { oldTransaction ->
                Redoable(
                    redo = categoryAmounts
                        .let { transactionsRepo.pushTransactionCAs(id, it) },
                    undo = transactionsRepo.pushTransactionCAs(
                        id,
                        oldTransaction.categoryAmounts
                    )
                )
                    .also { it.redo.subscribeBy(onComplete = { undoQueue.add(it) }) }
            })
    }
    fun undo() {
        (undoQueue.takeLast() ?: return)
            .also { it.undo.subscribeBy(onComplete = { redoQueue.add(it) }) }
    }
    fun redo() {
        (redoQueue.takeLast() ?: return)
            .also { it.redo.doOnComplete { undoQueue.add(it) }.subscribe() }
    }
    // # Internal
    private val undoQueue = SourceArrayList<Redoable>()
    private val redoQueue = SourceArrayList<Redoable>()
    // # Output
    val isUndoAvailable =
        undoQueue.observable
            .map { it.isNotEmpty() }
    val isRedoAvailable =
        redoQueue.observable
            .map { it.isNotEmpty() }

//    private val activeCA = mutableMapOf<Category, BigDecimal>()
//    override val transactionBox: Observable<Box<Transaction?>> =
//        transactionsDomain.uncategorizedSpends
//            .map { Box(it.getOrNull(0)) }
//    override fun finishTransactionWithCategory(category: Category) {
//        transactionBox
//            .observeOn(Schedulers.io())
//            .take(1)
//            .unbox()
//            .doOnNext { activeCA[category] = it.amount - activeCA.map{ it.value }.fold(0.toBigDecimal()) { acc, v -> acc + v } }
//            .flatMapCompletable { pushTransactionCAs(it.id, activeCA) }
//            .doOnComplete { activeCA.clear() }
//            .subscribe()
//    }
//    override val hasUncategorizedTransaction: Observable<Boolean> =
//        transactionBox
//            .map { it.unbox != null }
//
//
//    private val undoQueueIntents = PublishSubject.create<UndoQueueIntent>()
//    sealed class UndoQueueIntent {
//        object UndoLatest: UndoQueueIntent()
//        class Add(val lambda: () -> Unit): UndoQueueIntent()
//    }
//
//    private val undoQueue = undoQueueIntents
//        .scan(listOf<() -> Unit>()) { acc, it ->
//            when (it) {
//                is UndoQueueIntent.Add -> acc + it.lambda
//                is UndoQueueIntent.UndoLatest -> {
//                    if (acc.isNotEmpty())
//                        acc.last()()
//                    acc.dropLast(1)
//                }
//            }
//        }
//    val isUndoAvailable: Observable<Boolean> = undoQueue
//        .map { it.isNotEmpty() }
//        .replay(1).also { it.connect() }
//    fun pushTransactionCAs(id: String, categoryAmount: Map<Category, BigDecimal>): Completable {
//        return transactionsRepo.getTransaction(id)
//            .flatMapCompletable { oldTransaction ->
//                transactionsRepo.pushTransactionCAs(
//                    id,
//                    categoryAmount,
//                )
//                    .doOnComplete {
//                        undoQueueIntents.onNext(UndoQueueIntent.Add {
//                            transactionsRepo.pushTransactionCAs(
//                                id,
//                                oldTransaction.categoryAmounts,
//                            )
//                                .doOnComplete {
//
//                                }
//                                .subscribe()
//                        })
//                    }
//            }
//    }
//    fun undo() {
//        undoQueueIntents.onNext(UndoQueueIntent.UndoLatest)
//    }
//
//    privateVal redoQueueIntents
//
//    fun redo() {
//        TODO("Not yet implemented")
//    }
}