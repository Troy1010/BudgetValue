package com.tminus1010.budgetvalue.transactions.data

import com.tminus1010.budgetvalue._core.data.MiscDAO
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.transactions.domain.models.TransactionListDomainModel
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
    @Deprecated("Use transactions2")
    val transactions: Observable<List<Transaction>> =
        miscDAO.fetchTransactions().subscribeOn(Schedulers.io())
            .map { it.map { Transaction.fromDTO(it, categoryAmountsConverter) } }
            .replay(1).refCount()

    val transactions2: Observable<TransactionListDomainModel> =
        miscDAO.fetchTransactions().subscribeOn(Schedulers.io())
            .map { TransactionListDomainModel(it, categoryAmountsConverter) }
            .replay(1).refCount()

    fun tryPush(transaction: Transaction): Completable =
        miscDAO.tryAdd(transaction.toDTO(categoryAmountsConverter)).subscribeOn(Schedulers.io())

    fun push(transaction: Transaction): Completable =
        miscDAO.add(transaction.toDTO(categoryAmountsConverter)).subscribeOn(Schedulers.io())

    fun delete(transaction: Transaction): Completable =
        miscDAO.delete(transaction.toDTO(categoryAmountsConverter)).subscribeOn(Schedulers.io())

    fun update(transaction: Transaction): Completable =
        miscDAO.update(transaction.toDTO(categoryAmountsConverter)).subscribeOn(Schedulers.io())

    fun tryPush(transactions: List<Transaction>): Completable =
        miscDAO.tryAdd(transactions.map { it.toDTO(categoryAmountsConverter) }).subscribeOn(Schedulers.io())

    fun clear() = miscDAO.clearTransactions().subscribeOn(Schedulers.io())

    fun findTransactionsWithDescription(description: String): Single<List<Transaction>> =
        miscDAO.fetchTransactions(description).subscribeOn(Schedulers.io())
            .map { it.map { Transaction.fromDTO(it, categoryAmountsConverter) } }

    fun getTransaction(id: String): Single<Transaction> =
        miscDAO.getTransaction(id).subscribeOn(Schedulers.io())
            .map { Transaction.fromDTO(it, categoryAmountsConverter) }
}