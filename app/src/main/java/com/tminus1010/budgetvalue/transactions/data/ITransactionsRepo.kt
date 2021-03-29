package com.tminus1010.budgetvalue.transactions.data

import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.transactions.models.Transaction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

interface ITransactionsRepo {
    val transactions: Observable<List<Transaction>>
    fun tryPush(transaction: Transaction): Completable
    fun tryPush(transactions: List<Transaction>): Completable
    fun pushTransactionCA(transaction: Transaction, category: Category, amount: BigDecimal?): Completable
    fun pushTransactionCAs(transaction: Transaction, categoryAmounts: Map<Category, BigDecimal>): Completable
}