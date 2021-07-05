package com.tminus1010.budgetvalue.transactions.data

import com.tminus1010.budgetvalue.transactions.models.Transaction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface ITransactionsRepo {
    val transactions: Observable<List<Transaction>>
    fun tryPush(transaction: Transaction): Completable
    fun push(transaction: Transaction): Completable
    fun delete(transaction: Transaction): Completable
    fun tryPush(transactions: List<Transaction>): Completable
    fun findTransactionsWithDescription(description: String): Single<List<Transaction>>
    fun getTransaction(id: String): Single<Transaction>
    fun update(transaction: Transaction): Completable
}