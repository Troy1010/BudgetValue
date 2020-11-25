package com.example.budgetvalue.layer_data

import com.example.budgetvalue.model_app.Transaction
import io.reactivex.rxjava3.core.Observable

interface IMyDaoWrapper: MyDao {
    fun getTransactions(): Observable<List<Transaction>>
}