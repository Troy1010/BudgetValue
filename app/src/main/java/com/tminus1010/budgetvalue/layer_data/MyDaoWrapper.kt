package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.extensions.noEnd
import com.tminus1010.budgetvalue.model_app.Plan
import com.tminus1010.budgetvalue.model_app.Reconciliation
import com.tminus1010.budgetvalue.model_data.Account
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
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

    override val plans = myDao.fetchPlanReceived()
        .subscribeOn(Schedulers.io())
        .map { it.map { it.toPlan(typeConverter) } }
        .noEnd().replay(1).refCount()

    override fun pushPlan(plan: Plan) = myDao.add(plan.toPlanReceived(typeConverter)).subscribeOn(Schedulers.io())

    override fun pushReconciliation(reconciliation: Reconciliation): Completable {
        return reconciliation
            .toReconciliationReceived(typeConverter, BigDecimal(0))
            .let {
                myDao.add(it)
                    .subscribeOn(Schedulers.io())
            }
    }

    override fun fetchReconciliations(): Observable<List<Reconciliation>> {
        return myDao.fetchReconciliationReceived()
            .map { it.map { it.toReconciliation(typeConverter) } }
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