package com.example.budgetvalue.layer_data

import com.example.budgetvalue.SourceHashMap
import com.example.budgetvalue.extensions.noEnd
import com.example.budgetvalue.model_app.Category
import com.example.budgetvalue.model_data.Account
import com.example.budgetvalue.model_data.PlanCategoryAmount
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import javax.inject.Inject

class MyDaoWrapper @Inject constructor(
    val myDao: MyDao,
    val typeConverter: TypeConverter,
) : MyDao by myDao, IMyDaoWrapper {
    override val transactions = myDao.getTransactionsReceived()
        .map(typeConverter::transactions)
        .replay(1).refCount()

    override val planCategoryAmounts = myDao
        .getPlanCategoryAmountsReceived()
        .take(1)
        .subscribeOn(Schedulers.io())
        .map(typeConverter::categoryAmounts)
        .doOnNext(::bindToPlanCategoryAmounts)
        .noEnd().replay(1).refCount()

    private fun bindToPlanCategoryAmounts(map: SourceHashMap<Category, BigDecimal>) {
        map.additions.observeOn(Schedulers.io())
            .flatMap { kv -> kv.value.distinctUntilChanged().skip(1).map { Pair(kv.key, it) } }
            .subscribe {
                myDao.update(PlanCategoryAmount(it))
                    .subscribeOn(Schedulers.io()).blockingAwait()
            } // TODO("Handle observables")
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