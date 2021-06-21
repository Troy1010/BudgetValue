package com.tminus1010.budgetvalue.transactions.domain

import com.tminus1010.budgetvalue._core.middleware.unbox
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.data.ITransactionsRepo
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.tmcommonkotlin.rx.extensions.unbox
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategorizeTransactionsDomain @Inject constructor(
    private val transactionsRepo: ITransactionsRepo,
    transactionsDomain: TransactionsDomain,
) : ICategorizeTransactionsDomain {

    private val activeCA = mutableMapOf<Category, BigDecimal>()
    override val transactionBox: Observable<Box<Transaction?>> =
        transactionsDomain.uncategorizedSpends
            .map { Box(it.getOrNull(0)) }
    override fun finishTransactionWithCategory(category: Category) {
        transactionBox
            .observeOn(Schedulers.io())
            .take(1)
            .unbox()
            .doOnNext { activeCA[category] = it.amount - activeCA.map{ it.value }.fold(0.toBigDecimal()) { acc, v -> acc + v } }
            .flatMapCompletable { transactionsRepo.pushTransactionCAs(it.id, activeCA) }
            .doOnComplete { activeCA.clear() }
            .subscribe()
    }
    override val hasUncategorizedTransaction: Observable<Boolean> =
        transactionBox
            .map { it.unbox != null }
    private val undoQueue = mutableListOf<() -> Unit>()
    fun pushTransactionCAs(id: String, categoryAmount: Map<Category, BigDecimal>): Completable {
        var oldTransaction: Transaction? = null
        return transactionsRepo.getTransaction(id)
            .doOnSuccess { oldTransaction = it }
            .flatMapCompletable {
                transactionsRepo.pushTransactionCAs(
                    id,
                    categoryAmount,
                )
            }
            .doOnComplete {
                undoQueue.add {
                    transactionsRepo.pushTransactionCAs(
                        id,
                        oldTransaction!!.categoryAmounts,
                    )
                }
            }
    }
    fun undo() {
        if (undoQueue.isNotEmpty())
            undoQueue.last()()
        undoQueue.dropLast(1)
    }
}