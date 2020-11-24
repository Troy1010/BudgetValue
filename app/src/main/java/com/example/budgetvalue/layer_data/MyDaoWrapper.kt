package com.example.budgetvalue.layer_data

import com.example.budgetvalue.model_app.Category
import com.example.budgetvalue.model_app.Transaction
import io.reactivex.rxjava3.core.Observable

class MyDaoWrapper(
    val myDao: MyDao,
    val transformCategoryAction: (String) -> Category
) : MyDao by myDao, IMyDaoWrapper {
    override fun getTransactions2(): Observable<List<Transaction>> {
        return myDao.getTransactions()
            .map { it.map { it.toTransaction(transformCategoryAction) } }
    }
}