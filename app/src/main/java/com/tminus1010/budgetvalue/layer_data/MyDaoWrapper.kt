package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.extensions.noEnd
import com.tminus1010.budgetvalue.model_data.Category
import com.tminus1010.budgetvalue.model_app.Plan
import com.tminus1010.budgetvalue.model_app.Reconciliation
import com.tminus1010.budgetvalue.model_app.Transaction
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
    override val transactions =
        myDao.getTransactionsReceived()
            .map(typeConverter::transactions)
            .replay(1).refCount()

    override fun pushTransactionCA(transaction: Transaction, category: Category, amount: BigDecimal?) {
        transaction.categoryAmounts
            .toMutableMap()
            .apply { if (amount==null) remove(category) else put(category, amount) }
            .also { updateTransactionCategoryAmounts(transaction.id, it.mapKeys { it.key.name }).subscribe() }
    }

    override val plans = myDao.fetchPlanReceived()
        .subscribeOn(Schedulers.io())
        .map { it.map { it.toPlan(typeConverter) } }
        .noEnd().replay(1).refCount()

    override fun pushPlan(plan: Plan) = myDao.add(plan.toPlanReceived(typeConverter)).subscribeOn(Schedulers.io())
    override fun pushPlanCA(plan: Plan, category: Category, amount: BigDecimal?) {
        plan.categoryAmounts
            .toMutableMap()
            .apply { if (amount==null) remove(category) else put(category, amount) }
            .also { updatePlanCategoryAmounts(plan.toPlanReceived(typeConverter).startDate, it.mapKeys { it.key.name }).subscribe() }
    }

    override fun pushReconciliation(reconciliation: Reconciliation): Completable =
        reconciliation.toReconciliationReceived(typeConverter, BigDecimal(0))
            .let { myDao.add(it).subscribeOn(Schedulers.io()) }

    override fun pushReconciliationCA(
        reconciliation: Reconciliation,
        category: Category,
        amount: BigDecimal?,
    ) {
        reconciliation.categoryAmounts
            .toMutableMap()
            .apply { if (amount==null) remove(category) else put(category, amount) }
            .also { updateReconciliationCategoryAmounts(reconciliation.id, it.mapKeys { it.key.name }).subscribe() }
    }

    override val reconciliations: Observable<List<Reconciliation>> =
        myDao.fetchReconciliationReceived()
            .map { it.map { it.toReconciliation(typeConverter) } }
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