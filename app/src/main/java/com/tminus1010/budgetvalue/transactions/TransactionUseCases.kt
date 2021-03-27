package com.tminus1010.budgetvalue.transactions

import com.tminus1010.budgetvalue.categories.Category
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

interface TransactionUseCases {
    val transactions: Observable<List<Transaction>>
    fun tryPush(transaction: Transaction): Completable
    fun tryPush(transactions: List<Transaction>): Completable
    fun pushTransactionCA(
        transaction: Transaction,
        category: Category,
        amount: BigDecimal?
    ): Completable

    fun pushTransactionCAs(
        transaction: Transaction,
        categoryAmounts: Map<Category, BigDecimal>
    ): Completable
}