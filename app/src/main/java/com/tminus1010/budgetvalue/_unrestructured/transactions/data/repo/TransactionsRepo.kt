package com.tminus1010.budgetvalue._unrestructured.transactions.data.repo

import com.tminus1010.budgetvalue._unrestructured.transactions.app.Transaction
import com.tminus1010.budgetvalue._unrestructured.transactions.app.TransactionsAggregate
import com.tminus1010.budgetvalue.data.service.MiscDAO
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
) {
    val transactionsAggregate2 =
        miscDAO.fetchTransactionsFlow()
            .map { TransactionsAggregate(it) }
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)

    suspend fun push(transaction: Transaction) =
        miscDAO.push(transaction)

    suspend fun delete(id: String) =
        miscDAO.deleteTransaction(id)

    suspend fun update2(transaction: Transaction) =
        miscDAO.update(transaction)

    suspend fun clear() =
        miscDAO.clearTransactions()

    suspend fun getTransaction2(id: String) =
        miscDAO.getTransaction(id)
}