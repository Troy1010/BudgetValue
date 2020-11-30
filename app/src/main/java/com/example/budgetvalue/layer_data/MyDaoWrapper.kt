package com.example.budgetvalue.layer_data

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
        .noEnd().replay(1).refCount()

    override fun pushPlanCategoryAmount(categoryAmount: Pair<Category, BigDecimal>): Completable {
        return myDao.has(categoryAmount.first.name)
            .flatMapCompletable {
                if (it)
                    myDao.update(PlanCategoryAmount(categoryAmount))
                else
                    myDao.add(PlanCategoryAmount(categoryAmount))
            }
            .subscribeOn(Schedulers.io())
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