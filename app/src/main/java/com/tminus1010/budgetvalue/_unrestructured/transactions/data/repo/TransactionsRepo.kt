package com.tminus1010.budgetvalue._unrestructured.transactions.data.repo

import com.tminus1010.budgetvalue.data.service.MiscDAO
import com.tminus1010.budgetvalue._unrestructured.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue._unrestructured.transactions.app.Transaction
import com.tminus1010.budgetvalue._unrestructured.transactions.app.TransactionsAggregate
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionsRepo @Inject constructor(
    private val miscDAO: MiscDAO,
    private val categoryAmountsConverter: CategoryAmountsConverter,
) {
    @Deprecated("use transactionsAggregate2")
    val transactionsAggregate =
        miscDAO.fetchTransactions().subscribeOn(Schedulers.io())
            .map { TransactionsAggregate(it, categoryAmountsConverter) }
            .replay(1).refCount()

    val transactionsAggregate2 =
        miscDAO.fetchTransactionsFlow()
            .map { TransactionsAggregate(it, categoryAmountsConverter) }
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)

    fun push(transaction: Transaction) =
        miscDAO.push(transaction.toDTO(categoryAmountsConverter)).subscribeOn(Schedulers.io())

    fun delete(transaction: Transaction) =
        miscDAO.delete(transaction.toDTO(categoryAmountsConverter)).subscribeOn(Schedulers.io())

    @Deprecated("use update2")
    fun update(transaction: Transaction) =
        miscDAO.update(transaction.toDTO(categoryAmountsConverter)).subscribeOn(Schedulers.io())

    suspend fun update2(transaction: Transaction) =
        miscDAO.update2(transaction.toDTO(categoryAmountsConverter))

    fun clear() = miscDAO.clearTransactions().subscribeOn(Schedulers.io())

    fun getTransaction(id: String) =
        miscDAO.getTransaction(id).subscribeOn(Schedulers.io())
            .map { Transaction.fromDTO(it, categoryAmountsConverter) }

    suspend fun getTransaction2(id: String) =
        miscDAO.getTransaction2(id)?.let { Transaction.fromDTO(it, categoryAmountsConverter) }
}