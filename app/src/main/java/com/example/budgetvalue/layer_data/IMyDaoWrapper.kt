package com.example.budgetvalue.layer_data

import com.example.budgetvalue.model_app.Transaction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface IMyDaoWrapper: MyDao {
    fun getTransactions2(): Observable<List<Transaction>>
    fun add(transaction: Transaction): Completable
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("addTransactions")
    fun add(transactions: List<Transaction>): Completable
}