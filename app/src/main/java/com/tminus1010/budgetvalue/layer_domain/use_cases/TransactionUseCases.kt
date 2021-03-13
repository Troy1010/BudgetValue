package com.tminus1010.budgetvalue.layer_domain.use_cases

import com.tminus1010.budgetvalue.model_domain.Category
import com.tminus1010.budgetvalue.model_domain.Transaction
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