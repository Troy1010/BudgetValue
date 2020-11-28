package com.example.budgetvalue.layer_data

import com.example.budgetvalue.extensions.toSourceHashMap
import com.example.budgetvalue.model_app.Category
import com.example.budgetvalue.model_app.ICategoryParser
import com.example.budgetvalue.model_data.Account
import com.example.budgetvalue.model_data.PlanCategoryAmount
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

class MyDaoWrapper(
    val myDao: MyDao,
    val categoryParser: ICategoryParser
) : MyDao by myDao, IMyDaoWrapper {
    override val transactions = myDao.getTransactionsReceived()
        .map { it.map { it.toTransaction(categoryParser) } }
        .replay(1).refCount()

    override val planCategoryAmounts = myDao
        .getPlanCategoryAmountsReceived()
        .take(1)
        .subscribeOn(Schedulers.io())
        .map { it.associate { Pair(categoryParser.parseCategory(it.categoryName), it.amount) } }
        .map { it.toSourceHashMap() }
        .doOnNext { it.observable.observeOn(Schedulers.io()).subscribe(::bindToPlanCategoryAmounts) }
        .replay(1).refCount()

    private fun bindToPlanCategoryAmounts(itemObservablesObservable: Map<Category, BehaviorSubject<BigDecimal>>) {
        myDao.clearPlanCategoryAmounts().blockingAwait()
        for ((category, amountBehaviorSubject) in itemObservablesObservable) {
            myDao.add(PlanCategoryAmount(category, BigDecimal.ZERO)).subscribeOn(Schedulers.io()).blockingAwait()
            amountBehaviorSubject.observeOn(Schedulers.io())
                .subscribe { // TODO("Handle disposables")
                    myDao.update(PlanCategoryAmount(category, it)).subscribeOn(Schedulers.io()).blockingAwait()
                }
        }
    }

    override fun update(account: Account): Completable {
        return myDao
            .getAccount(account.id)
            .take(1)
            .filter { it != account }
            .flatMapCompletable { myDao.update(account) }
            .subscribeOn(Schedulers.io())
    }
}