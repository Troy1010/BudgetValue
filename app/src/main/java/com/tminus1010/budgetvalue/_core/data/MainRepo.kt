package com.tminus1010.budgetvalue._core.data

import com.tminus1010.budgetvalue._core.middleware.toBigDecimalSafe
import com.tminus1010.budgetvalue.accounts.models.Account
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.categories.data.CategoriesRepo
import com.tminus1010.budgetvalue.categories.data.ICategoriesRepo
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.models.Plan
import com.tminus1010.budgetvalue.reconciliations.models.Reconciliation
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.tmcommonkotlin.misc.extensions.associate
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DTO <-> DomainModel mapping happens here, b/c, why not.
 */
@Singleton
class MainRepo @Inject constructor(
    private val sharedPrefWrapper: SharedPrefWrapper,
    private val miscDAO: MiscDAO,
    private val categoryAmountsConverter: CategoryAmountsConverter,
    private val categoryParser: ICategoryParser,
    private val categoriesRepo: CategoriesRepo
) : IMainRepo, ICategoriesRepo by categoriesRepo {
    override fun fetchAccounts(): Observable<List<Account>> =
        miscDAO.fetchAccounts().map { it.map { Account.fromDTO(it) } }

    override fun update(account: Account): Completable =
        miscDAO.updateAccount(account.toDTO())

    override fun push(account: Account): Completable =
        miscDAO.addAccount(account.toDTO())

    override fun delete(account: Account): Completable =
        miscDAO.deleteAccount(account.toDTO())

    override fun fetchAppInitBool(): Boolean =
        sharedPrefWrapper.fetchAppInitBool()

    override fun pushAppInitBool(appInitBool: Boolean) =
        sharedPrefWrapper.pushAppInitBool(appInitBool)

    override fun pushActiveReconciliationCA(kv: Pair<Category, BigDecimal?>): Completable =
        sharedPrefWrapper.pushActiveReconciliationCA(Pair(kv.first.name, kv.second.toString()))

    override val anchorDateOffset: Observable<Long> =
        sharedPrefWrapper.anchorDateOffset

    override fun pushAnchorDateOffset(anchorDateOffset: Long?): Completable =
        sharedPrefWrapper.pushAnchorDateOffset(anchorDateOffset)

    override val blockSize: Observable<Long> =
        sharedPrefWrapper.blockSize

    override fun pushBlockSize(blockSize: Long?): Completable =
        sharedPrefWrapper.pushBlockSize(blockSize)

    override fun pushReconciliationCA(reconciliation: Reconciliation, category: Category, amount: BigDecimal?, ) =
        reconciliation.categoryAmounts
            .toMutableMap()
            .apply { if (amount==null) remove(category) else put(category, amount) }
            .let { miscDAO.updateReconciliationCategoryAmounts(reconciliation.id, it.mapKeys { it.key.name }) }

    override fun clearReconciliations() = miscDAO.clearReconciliations()

    override fun push(reconciliation: Reconciliation): Completable =
        miscDAO.add(reconciliation.toDTO(categoryAmountsConverter))

    override fun delete(reconciliation: Reconciliation): Completable =
        miscDAO.delete(reconciliation.toDTO(categoryAmountsConverter))

    override fun delete(plan: Plan): Completable =
        miscDAO.delete(plan.toDTO(categoryAmountsConverter))

    override val reconciliations: Observable<List<Reconciliation>> =
        miscDAO.fetchReconciliations()
            .map { it.map { Reconciliation.fromDTO(it, categoryAmountsConverter) } }
            .replay(1).refCount()

    override val activeReconciliationCAs: Observable<Map<Category, BigDecimal>> =
        sharedPrefWrapper.activeReconciliationCAs
            .map { it.associate { categoryParser.parseCategory(it.key) to it.value.toBigDecimalSafe() } }
            .replay(1).refCount()
            .subscribeOn(Schedulers.io())

    override fun pushActiveReconciliationCAs(categoryAmounts: Map<Category, BigDecimal>): Completable =
        sharedPrefWrapper.pushActiveReconciliationCAs(categoryAmounts.associate { it.key.name to it.value.toString() })

    override fun clearActiveReconcileCAs(): Completable =
        sharedPrefWrapper.clearActiveReconcileCAs()
            .subscribeOn(Schedulers.io())

    override val transactions: Observable<List<Transaction>> =
        miscDAO.fetchTransactions()
            .map { it.map { Transaction.fromDTO(it, categoryAmountsConverter) } }
            .replay(1).refCount().subscribeOn(Schedulers.io())

    override fun tryPush(transaction: Transaction): Completable =
        miscDAO.tryAdd(transaction.toDTO(categoryAmountsConverter))

    override fun push(transaction: Transaction): Completable =
        miscDAO.add(transaction.toDTO(categoryAmountsConverter))

    override fun delete(transaction: Transaction): Completable =
        miscDAO.delete(transaction.toDTO(categoryAmountsConverter))

    override fun update(transaction: Transaction): Completable =
        miscDAO.update(transaction.toDTO(categoryAmountsConverter))
            .subscribeOn(Schedulers.io())

    override fun tryPush(transactions: List<Transaction>): Completable =
        miscDAO.tryAdd(transactions.map { it.toDTO(categoryAmountsConverter) })

    override fun findTransactionsWithDescription(description: String): Single<List<Transaction>> =
        miscDAO.fetchTransactions(description).map { it.map { Transaction.fromDTO(it, categoryAmountsConverter) } }

    override fun getTransaction(id: String): Single<Transaction> =
        miscDAO.getTransaction(id)
            .map { Transaction.fromDTO(it, categoryAmountsConverter) }
            .subscribeOn(Schedulers.io())

    override val plans: Observable<List<Plan>> =
        miscDAO.fetchPlans().map { it.map { Plan.fromDTO(it, categoryAmountsConverter) } }

    override fun pushPlan(plan: Plan): Completable =
        miscDAO.add(plan.toDTO(categoryAmountsConverter))

    override fun updatePlanCA(plan: Plan, category: Category, amount: BigDecimal?): Completable =
        plan.categoryAmounts
            .toMutableMap()
            .apply { if (amount == null) remove(category) else put(category, amount) }
            .let {
                miscDAO.updatePlanCategoryAmounts(
                    plan.toDTO(categoryAmountsConverter).startDate,
                    it.mapKeys { it.key.name })
            }

    override fun updatePlanCAs(plan: Plan, categoryAmounts: Map<String, BigDecimal>): Completable =
        miscDAO.updatePlanCategoryAmounts(plan.toDTO(categoryAmountsConverter).startDate, categoryAmounts)

    override fun updatePlanAmount(plan: Plan, amount: BigDecimal): Completable =
        miscDAO.updatePlanAmount(plan.toDTO(categoryAmountsConverter).startDate, amount)
}