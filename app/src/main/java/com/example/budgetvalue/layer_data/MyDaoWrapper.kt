package com.example.budgetvalue.layer_data

import com.example.budgetvalue.model_app.IParseCategory
import com.example.budgetvalue.model_data.Account
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

class MyDaoWrapper(
    val myDao: MyDao,
    val parseCategory: IParseCategory
) : MyDao by myDao, IMyDaoWrapper {
    override val transactions = myDao.getTransactionsReceived()
        .map { it.map { it.toTransaction(parseCategory) } }
        .replay(1).refCount()

    override val planCategoryAmounts = myDao
        .getPlanCategoryAmountsReceived()
        .map { it.associate { Pair(parseCategory.parseCategory(it.categoryName), it.amount) } }
        .replay(1).refCount()

    override fun update(account: Account): Completable {
        return myDao
            .getAccount(account.id)
            .take(1)
            .filter { it != account }
            .flatMapCompletable { myDao.update(account) }
            .subscribeOn(Schedulers.io())
    }
}