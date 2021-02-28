package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.model_data.AccountDTO
import com.tminus1010.budgetvalue.model_data.Category
import com.tminus1010.budgetvalue.model_domain.Plan
import com.tminus1010.budgetvalue.model_domain.Reconciliation
import com.tminus1010.budgetvalue.model_domain.Transaction
import com.tminus1010.tmcommonkotlin.rx.extensions.noEnd
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import javax.inject.Inject

class RepoWrapper @Inject constructor(
    val repo: Repo,
    val typeConverter: TypeConverter,
) : IRepoWrapper {
    override val transactions =
        repo.getTransactionsReceived()
            .map { it.map { it.toTransaction(typeConverter) } }
            .replay(1).refCount()

    override fun pushTransactionCA(transaction: Transaction, category: Category, amount: BigDecimal?) {
        transaction.categoryAmounts
            .toMutableMap()
            .apply { if (amount==null) remove(category) else put(category, amount) }
            .also { repo.updateTransactionCategoryAmounts(transaction.id, it.mapKeys { it.key.name }).subscribe() }
    }

    override val plans = repo.fetchPlanReceived()
        .subscribeOn(Schedulers.io())
        .map { it.map { it.toPlan(typeConverter) } }
        .noEnd().replay(1).refCount()

    override fun pushPlan(plan: Plan) =
        repo.add(plan.toPlanReceived(typeConverter))
            .subscribeOn(Schedulers.io())

    override fun pushPlanCA(plan: Plan, category: Category, amount: BigDecimal?) {
        plan.categoryAmounts
            .toMutableMap()
            .apply { if (amount==null) remove(category) else put(category, amount) }
            .also { repo.updatePlanCategoryAmounts(plan.toPlanReceived(typeConverter).startDate, it.mapKeys { it.key.name }).subscribe() }
    }

    override fun pushReconciliation(reconciliation: Reconciliation): Completable =
        reconciliation.toReconciliationReceived(typeConverter, BigDecimal(0))
            .let { repo.add(it).subscribeOn(Schedulers.io()) }

    override fun pushReconciliationCA(
        reconciliation: Reconciliation,
        category: Category,
        amount: BigDecimal?,
    ) {
        reconciliation.categoryAmounts
            .toMutableMap()
            .apply { if (amount==null) remove(category) else put(category, amount) }
            .also { repo.updateReconciliationCategoryAmounts(reconciliation.id, it.mapKeys { it.key.name }).subscribe() }
    }

    override val reconciliations: Observable<List<Reconciliation>> =
        repo.fetchReconciliationReceived()
            .map { it.map { it.toReconciliation(typeConverter) } }
            .replay(1).refCount()

    override fun update(accountDTO: AccountDTO): Completable =
        repo
            .getAccount(accountDTO.id)
            .take(1)
            .filter { it != accountDTO }
            .flatMapCompletable { repo.update(accountDTO) }
            .subscribeOn(Schedulers.io())
}