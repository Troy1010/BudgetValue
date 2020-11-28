package com.example.budgetvalue.layer_data

import com.example.budgetvalue.SourceHashMap
import com.example.budgetvalue.model_app.Category
import com.example.budgetvalue.model_data.Account
import com.example.budgetvalue.model_data.PlanCategoryAmount
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import javax.inject.Inject

class MyDaoWrapper @Inject constructor(
    val myDao: MyDao,
    val typeConverterUtil: TypeConverterUtil
) : MyDao by myDao, IMyDaoWrapper {
    override val transactions = myDao.getTransactionsReceived()
        .map(typeConverterUtil::transactions)
        .replay(1).refCount()

    override val planCategoryAmounts = myDao
        .getPlanCategoryAmountsReceived()
        .take(1)
        .subscribeOn(Schedulers.io())
        .map(typeConverterUtil::categoryAmounts)
        .doOnNext(::bindToPlanCategoryAmounts)
        .replay(1).refCount()

    private fun bindToPlanCategoryAmounts(map: SourceHashMap<Category, BigDecimal>) {
        map.observable.observeOn(Schedulers.io())
            .subscribe { // TODO("every emission, I do double subscriptions")
                for ((category, amountBehaviorSubject) in it) {
                    amountBehaviorSubject.observeOn(Schedulers.io())
                        .subscribe { // TODO("Handle disposables")
                            myDao.update(PlanCategoryAmount(category, it)).subscribeOn(Schedulers.io())
                                .blockingAwait()
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