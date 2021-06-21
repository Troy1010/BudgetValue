package com.tminus1010.budgetvalue.transactions.data

import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.Transaction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.math.BigDecimal

interface ITransactionsRepo {
    val transactions: Observable<List<Transaction>>
    fun tryPush(transaction: Transaction): Completable
    fun push(transaction: Transaction): Completable
    fun delete(transaction: Transaction): Completable
    fun tryPush(transactions: List<Transaction>): Completable
    fun pushTransactionCA(transaction: Transaction, category: Category, amount: BigDecimal?): Completable
    fun pushTransactionCAs(id: String, categoryAmounts: Map<Category, BigDecimal>): Completable
    fun findTransactionsWithDescription(description: String): Single<List<Transaction>>
    fun getTransaction(id: String): Single<Transaction>
}