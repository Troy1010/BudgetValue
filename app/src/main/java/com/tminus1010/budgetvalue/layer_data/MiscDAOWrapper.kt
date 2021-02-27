package com.tminus1010.budgetvalue.layer_data


import com.tminus1010.budgetvalue.model_data.Category
import com.tminus1010.budgetvalue.model_app.Plan
import com.tminus1010.budgetvalue.model_app.Reconciliation
import com.tminus1010.budgetvalue.model_app.Transaction
import com.tminus1010.budgetvalue.model_data.AccountDTO
import com.tminus1010.tmcommonkotlin.rx.extensions.noEnd
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import javax.inject.Inject

class MiscDAOWrapper @Inject constructor(
    val miscDAO: MiscDAO,
    val typeConverter: TypeConverter,
) : MiscDAO by miscDAO, IMiscDAOWrapper {
    override val transactions =
        miscDAO.getTransactionsReceived()
            .map { it.map { it.toTransaction(typeConverter) } }
            .replay(1).refCount()

    override fun pushTransactionCA(transaction: Transaction, category: Category, amount: BigDecimal?) {
        transaction.categoryAmounts
            .toMutableMap()
            .apply { if (amount==null) remove(category) else put(category, amount) }
            .also { updateTransactionCategoryAmounts(transaction.id, it.mapKeys { it.key.name }).subscribe() }
    }

    override val plans = miscDAO.fetchPlanReceived()
        .subscribeOn(Schedulers.io())
        .map { it.map { it.toPlan(typeConverter) } }
        .noEnd().replay(1).refCount()

    override fun pushPlan(plan: Plan) = miscDAO.add(plan.toPlanReceived(typeConverter)).subscribeOn(Schedulers.io())
    override fun pushPlanCA(plan: Plan, category: Category, amount: BigDecimal?) {
        plan.categoryAmounts
            .toMutableMap()
            .apply { if (amount==null) remove(category) else put(category, amount) }
            .also { updatePlanCategoryAmounts(plan.toPlanReceived(typeConverter).startDate, it.mapKeys { it.key.name }).subscribe() }
    }

    override fun pushReconciliation(reconciliation: Reconciliation): Completable =
        reconciliation.toReconciliationReceived(typeConverter, BigDecimal(0))
            .let { miscDAO.add(it).subscribeOn(Schedulers.io()) }

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
        miscDAO.fetchReconciliationReceived()
            .map { it.map { it.toReconciliation(typeConverter) } }
            .replay(1).refCount()

    override fun update(accountDTO: AccountDTO): Completable {
        return miscDAO
            .getAccount(accountDTO.id)
            .take(1)
            .filter { it != accountDTO }
            .flatMapCompletable { miscDAO.update(accountDTO) }
            .subscribeOn(Schedulers.io())
    }
}