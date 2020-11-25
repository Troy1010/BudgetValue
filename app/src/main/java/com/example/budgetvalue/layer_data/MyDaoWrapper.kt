package com.example.budgetvalue.layer_data

import com.example.budgetvalue.model_app.IParseCategory
import com.example.budgetvalue.model_app.Transaction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

class MyDaoWrapper(
    val myDao: MyDao,
    val parseCategory: IParseCategory
) : MyDao by myDao, IMyDaoWrapper {
    override fun getTransactions2(): Observable<List<Transaction>> {
        return myDao.getTransactions()
            .map { it.map { it.toTransaction(parseCategory) } }
    }

    override fun add(transaction: Transaction): Completable {
        return myDao.add(transaction.toTransactionReceived())
    }

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("addTransactions")
    override fun add(transactions: List<Transaction>): Completable {
        return myDao.add(transactions.map { it.toTransactionReceived() } )
    }

}