package com.tminus1010.budgetvalue.transactions.data

import com.tminus1010.budgetvalue._core.data.MiscDAO
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.transactions.models.Transaction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionsRepo @Inject constructor(
    private val miscDAO: MiscDAO,
    private val categoryAmountsConverter: CategoryAmountsConverter,
) {
    val transactions: Observable<List<Transaction>> =
        miscDAO.fetchTransactions()
            .map { it.map { Transaction.fromDTO(it, categoryAmountsConverter) } }
            .replay(1).refCount()
            .subscribeOn(Schedulers.io())

    fun tryPush(transaction: Transaction): Completable =
        miscDAO.tryAdd(transaction.toDTO(categoryAmountsConverter))
            .subscribeOn(Schedulers.io())

    fun push(transaction: Transaction): Completable =
        miscDAO.add(transaction.toDTO(categoryAmountsConverter))
            .subscribeOn(Schedulers.io())

    fun delete(transaction: Transaction): Completable =
        miscDAO.delete(transaction.toDTO(categoryAmountsConverter))
            .subscribeOn(Schedulers.io())

    fun update(transaction: Transaction): Completable =
        miscDAO.update(transaction.toDTO(categoryAmountsConverter))
            .subscribeOn(Schedulers.io())

    fun tryPush(transactions: List<Transaction>): Completable =
        miscDAO.tryAdd(transactions.map { it.toDTO(categoryAmountsConverter) })
            .subscribeOn(Schedulers.io())

    fun findTransactionsWithDescription(description: String): Single<List<Transaction>> =
        miscDAO.fetchTransactions(description)
            .map { it.map { Transaction.fromDTO(it, categoryAmountsConverter) } }
            .subscribeOn(Schedulers.io())

    fun getTransaction(id: String): Single<Transaction> =
        miscDAO.getTransaction(id)
            .map { Transaction.fromDTO(it, categoryAmountsConverter) }
            .subscribeOn(Schedulers.io())
}