package com.example.budgetvalue.layer_data

import com.example.budgetvalue.SourceHashMap
import com.example.budgetvalue.extensions.toSourceHashMap
import com.example.budgetvalue.model_app.Category
import com.example.budgetvalue.model_app.ICategoryParser
import com.example.budgetvalue.model_data.Account
import com.example.budgetvalue.model_data.PlanCategoryAmount
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
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
        .doOnNext(::bindToPlanCategoryAmounts)
        .replay(1).refCount()

    private fun bindToPlanCategoryAmounts(categoryAmounts: SourceHashMap<Category, BigDecimal>) {
        synchronized(categoryAmounts) {
            myDao.clearPlanCategoryAmounts().blockingAwait()
            categoryAmounts.itemObservablesObservable.take(1).subscribe {
                for ((category, amountBehaviorSubject) in it) {
                    myDao.add(PlanCategoryAmount(category, BigDecimal.ZERO)).subscribe()
                    amountBehaviorSubject.observeOn(Schedulers.io())
                        .subscribe { // TODO("Handle disposables")
                            myDao.update(PlanCategoryAmount(category, it)).subscribe()
                        }
                }
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