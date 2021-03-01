package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.model_domain.*
import com.tminus1010.tmcommonkotlin.rx.extensions.associate
import com.tminus1010.tmcommonkotlin.rx.extensions.noEnd
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import javax.inject.Inject

class RepoWrapper @Inject constructor(
    val repo: Repo,
    val typeConverter: TypeConverter,
    val categoryParser: ICategoryParser,
) : IRepoWrapper {
    override val transactions =
        repo.fetchTransactions()
            .map { it.map { Transaction.fromDTO(it, typeConverter) } }
            .replay(1).refCount()

    override fun tryPush(transaction: Transaction): Completable =
        repo.tryAdd(transaction.toDTO(typeConverter))

    override fun tryPush(transactions: List<Transaction>): Completable =
        repo.tryAdd(transactions.map { it.toDTO(typeConverter) })

    override fun pushTransactionCA(transaction: Transaction, category: Category, amount: BigDecimal?): Completable =
        transaction.categoryAmounts
            .toMutableMap()
            .apply { if (amount==null) remove(category) else put(category, amount) }
            .let { repo.updateTransactionCategoryAmounts(transaction.id, it.mapKeys { it.key.name }) }

    override fun pushTransactionCAs(transaction: Transaction, categoryAmounts: Map<Category, BigDecimal>) =
        repo.updateTransactionCategoryAmounts(transaction.id, categoryAmounts.mapKeys { it.key.name })

    override val plans =
        repo.fetchPlans()
            .subscribeOn(Schedulers.io())
            .map { it.map { Plan.fromDTO(it, typeConverter) } }
            .noEnd().replay(1).refCount()

    override fun pushPlan(plan: Plan) =
        repo.add(plan.toDTO(typeConverter))
            .subscribeOn(Schedulers.io())

    override fun pushPlanCA(plan: Plan, category: Category, amount: BigDecimal?): Completable =
        plan.categoryAmounts
            .toMutableMap()
            .apply { if (amount==null) remove(category) else put(category, amount) }
            .let { repo.updatePlanCategoryAmounts(plan.toDTO(typeConverter).startDate, it.mapKeys { it.key.name }) }

    override fun pushReconciliation(reconciliation: Reconciliation): Completable =
        reconciliation.toDTO(typeConverter, BigDecimal(0))
            .let { repo.add(it).subscribeOn(Schedulers.io()) }

    override fun pushReconciliationCA(reconciliation: Reconciliation, category: Category, amount: BigDecimal?, ) =
        reconciliation.categoryAmounts
            .toMutableMap()
            .apply { if (amount==null) remove(category) else put(category, amount) }
            .let { repo.updateReconciliationCategoryAmounts(reconciliation.id, it.mapKeys { it.key.name }) }

    override val reconciliations: Observable<List<Reconciliation>> =
        repo.fetchReconciliations()
            .map { it.map { Reconciliation.fromDTO(it, typeConverter) } }
            .replay(1).refCount()

    override val accounts: Observable<List<Account>> =
        repo.fetchAccounts()
            .map { it.map { typeConverter.toAccount(it) } }

    override fun update(account: Account): Completable =
        repo.updateAccount(account.toDTO())

    override fun push(account: Account): Completable =
        repo.addAccount(account.toDTO())

    override fun delete(account: Account): Completable =
        repo.deleteAccount(account.toDTO())

    override val activeReconciliationCAs: Observable<Map<Category, BigDecimal>> =
        repo.activeReconciliationCAs
            .map { it.associate { categoryParser.parseCategory(it.key) to it.value.toBigDecimal() } }

    override fun pushActiveReconciliationCAs(categoryAmounts: Map<Category, BigDecimal>): Completable =
        repo.pushActiveReconciliationCAs(categoryAmounts.associate { it.key.name to it.value.toString() })

    override fun pushActiveReconciliationCA(kv: Pair<Category, BigDecimal?>): Completable =
        repo.pushActiveReconciliationCA(Pair(kv.first.name, kv.second?.toString()))

    override fun clearActiveReconcileCAs(): Completable =
        repo.clearActiveReconcileCAs()

    override val activePlanCAs: Observable<Map<Category, BigDecimal>> =
        repo.activePlanCAs
            .map { it.associate { categoryParser.parseCategory(it.key) to it.value.toBigDecimal() } }

    override fun pushActivePlanCAs(categoryAmounts: Map<Category, BigDecimal>): Completable =
        repo.pushActivePlanCAs(categoryAmounts.associate { it.key.name to it.value.toString() })

    override fun pushActivePlanCA(kv: Pair<Category, BigDecimal?>): Completable =
        repo.pushActivePlanCA(Pair(kv.first.name, kv.second?.toString()))

    override fun clearActivePlan(): Completable =
        repo.clearActivePlanCAs()

    override fun fetchExpectedIncome(): BigDecimal =
        repo.fetchExpectedIncome().toBigDecimal()

    override fun pushExpectedIncome(expectedIncome: BigDecimal?): Completable =
        repo.pushExpectedIncome(expectedIncome.toString())

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