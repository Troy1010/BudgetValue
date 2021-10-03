package com.tminus1010.budgetvalue.transactions.data.repo

import com.tminus1010.budgetvalue._core.data.MiscDAO
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.transactions.app.TransactionsAggregate
import com.tminus1010.budgetvalue.transactions.models.Transaction
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionsRepo @Inject constructor(
    private val miscDAO: MiscDAO,
    private val categoryAmountsConverter: CategoryAmountsConverter,
) {
    val transactionsAggregate =
        miscDAO.fetchTransactions().subscribeOn(Schedulers.io())
            .map { TransactionsAggregate(it, categoryAmountsConverter) }
            .replay(1).refCount()

    fun tryPush(transaction: Transaction) =
        miscDAO.tryAdd(transaction.toDTO(categoryAmountsConverter)).subscribeOn(Schedulers.io())

    fun push(transaction: Transaction) =
        miscDAO.add(transaction.toDTO(categoryAmountsConverter)).subscribeOn(Schedulers.io())

    fun delete(transaction: Transaction) =
        miscDAO.delete(transaction.toDTO(categoryAmountsConverter)).subscribeOn(Schedulers.io())

    fun update(transaction: Transaction) =
        miscDAO.update(transaction.toDTO(categoryAmountsConverter)).subscribeOn(Schedulers.io())

    fun tryPush(transactions: List<Transaction>) =
        miscDAO.tryAdd(transactions.map { it.toDTO(categoryAmountsConverter) }).subscribeOn(Schedulers.io())

    fun clear() = miscDAO.clearTransactions().subscribeOn(Schedulers.io())

    fun findTransactionsWithDescription(description: String) =
        miscDAO.fetchTransactions(description).subscribeOn(Schedulers.io())
            .map { it.map { Transaction.fromDTO(it, categoryAmountsConverter) } }

    fun getTransaction(id: String) =
        miscDAO.getTransaction(id).subscribeOn(Schedulers.io())
            .map { Transaction.fromDTO(it, categoryAmountsConverter) }
}