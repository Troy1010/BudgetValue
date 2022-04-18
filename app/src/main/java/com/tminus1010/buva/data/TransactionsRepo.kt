package com.tminus1010.buva.data

import com.tminus1010.buva.data.service.MiscDAO
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.buva.domain.TransactionsAggregate
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
    val transactionsAggregate =
        miscDAO.fetchTransactions()
            .map(::TransactionsAggregate)
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)

    suspend fun push(transaction: Transaction) =
        miscDAO.push(transaction)

    suspend fun delete(id: String) =
        miscDAO.deleteTransaction(id)

    suspend fun clear() =
        miscDAO.clearTransactions()

    suspend fun getTransaction2(id: String) =
        miscDAO.getTransaction(id)
}