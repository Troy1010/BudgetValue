package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.extensions.fromJson
import com.tminus1010.budgetvalue.extensions.toJson
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.model_data.AccountDTO
import com.tminus1010.budgetvalue.model_data.Category
import com.tminus1010.budgetvalue.model_domain.Plan
import com.tminus1010.budgetvalue.model_domain.Reconciliation
import com.tminus1010.budgetvalue.model_domain.Transaction
import com.tminus1010.budgetvalue.moshi
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

    override fun pushTransactionCA(transaction: Transaction, category: Category, amount: BigDecimal?): Completable =
        transaction.categoryAmounts
            .toMutableMap()
            .apply { if (amount==null) remove(category) else put(category, amount) }
            .let { repo.updateTransactionCategoryAmounts(transaction.id, it.mapKeys { it.key.name }) }

    override val plans =
        repo.fetchPlanReceived()
            .subscribeOn(Schedulers.io())
            .map { it.map { it.toPlan(typeConverter) } }
            .noEnd().replay(1).refCount()

    override fun pushPlan(plan: Plan) =
        repo.add(plan.toPlanReceived(typeConverter))
            .subscribeOn(Schedulers.io())

    override fun pushPlanCA(plan: Plan, category: Category, amount: BigDecimal?): Completable =
        plan.categoryAmounts
            .toMutableMap()
            .apply { if (amount==null) remove(category) else put(category, amount) }
            .let { repo.updatePlanCategoryAmounts(plan.toPlanReceived(typeConverter).startDate, it.mapKeys { it.key.name }) }

    override fun pushReconciliation(reconciliation: Reconciliation): Completable =
        reconciliation.toReconciliationReceived(typeConverter, BigDecimal(0))
            .let { repo.add(it).subscribeOn(Schedulers.io()) }

    override fun pushReconciliationCA(reconciliation: Reconciliation, category: Category, amount: BigDecimal?, ) =
        reconciliation.categoryAmounts
            .toMutableMap()
            .apply { if (amount==null) remove(category) else put(category, amount) }
            .let { repo.updateReconciliationCategoryAmounts(reconciliation.id, it.mapKeys { it.key.name }) }

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

    override val activeReconciliationCAs: Observable<Map<Category, BigDecimal>> =
        repo.activeReconciliationCAs
            .map { moshi.fromJson(moshi.toJson(it)) }

    override fun pushActiveReconciliationCAs(categoryAmounts: Map<Category, BigDecimal>?): Completable =
        categoryAmounts
            .let { moshi.toJson(it) }
            .let { repo.pushActiveReconciliationCAs(moshi.fromJson(it)) } as Completable

    override fun pushActiveReconciliationCA(kv: Pair<Category, BigDecimal?>): Completable =
        repo.pushActiveReconciliationCA(Pair(kv.first.name, kv.second.toString()))

    override fun clearActiveReconcileCAs(): Completable =
        repo.clearActiveReconcileCAs()

    override val activePlanCAs: Observable<Map<Category, BigDecimal>> =
        repo.activePlanCAs
            .map { moshi.fromJson(moshi.toJson(it)) }

    override fun pushActivePlanCAs(categoryAmounts: Map<Category, BigDecimal>?): Completable =
        categoryAmounts
            .let { moshi.toJson(it) }
            .let { repo.pushActivePlanCAs(moshi.fromJson(it)) }

    override fun pushActivePlanCA(kv: Pair<Category, BigDecimal?>): Completable =
        repo.pushActivePlanCA(moshi.fromJson(moshi.toJson(kv)))

    override fun clearActivePlan(): Completable =
        repo.clearActivePlanCAs()

    override fun fetchExpectedIncome(): BigDecimal =
        repo.fetchExpectedIncome()
            .let { moshi.fromJson(it) }

    override fun pushExpectedIncome(expectedIncome: BigDecimal?): Completable =
        repo.pushExpectedIncome(moshi.toJson(expectedIncome))

    override val anchorDateOffset: Observable<Long> =
        repo.anchorDateOffset

    override fun pushAnchorDateOffset(anchorDateOffset: Long?): Completable =
        repo.pushAnchorDateOffset(anchorDateOffset)

    override val blockSize: Observable<Long> =
        repo.blockSize

    override fun pushBlockSize(blockSize: Long?): Completable =
        repo.pushBlockSize(blockSize)

    override fun fetchAppInitBool(): Boolean =
        repo.fetchAppInitBool()

    override fun pushAppInitBool(boolean: Boolean): Completable =
        repo.pushAppInitBool(boolean)
}