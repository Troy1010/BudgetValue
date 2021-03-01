package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.model_domain.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

interface IRepoWrapper {
    val transactions: Observable<List<Transaction>>
    fun tryPush(transaction: Transaction): Completable
    fun tryPush(transactions: List<Transaction>): Completable
    fun pushTransactionCA(transaction: Transaction, category: Category, amount: BigDecimal?): Completable
    fun pushTransactionCAs(transaction: Transaction, categoryAmounts: Map<Category, BigDecimal>): Completable
    val plans: Observable<List<Plan>>
    fun pushPlan(plan: Plan): Completable
    fun pushPlanCA(plan: Plan, category: Category, amount: BigDecimal?): Completable
    fun pushReconciliation(reconciliation: Reconciliation): Completable
    val reconciliations: Observable<List<Reconciliation>>
    fun pushReconciliationCA(reconciliation: Reconciliation, category: Category, amount: BigDecimal?): Completable
    val activeReconciliationCAs: Observable<Map<Category, BigDecimal>>
    fun pushActiveReconciliationCAs(categoryAmounts: Map<Category, BigDecimal>): Completable
    fun pushActiveReconciliationCA(kv: Pair<Category, BigDecimal?>): Completable
    fun clearActiveReconcileCAs(): Completable
    val activePlanCAs: Observable<Map<Category, BigDecimal>>
    fun pushActivePlanCAs(categoryAmounts: Map<Category, BigDecimal>): Completable
    fun pushActivePlanCA(kv: Pair<Category, BigDecimal?>): Completable
    fun clearActivePlan(): Completable
    fun fetchExpectedIncome(): BigDecimal
    fun pushExpectedIncome(expectedIncome: BigDecimal?): Completable
    val anchorDateOffset: Observable<Long>
    fun pushAnchorDateOffset(anchorDateOffset: Long?): Completable
    val blockSize: Observable<Long>
    fun pushBlockSize(blockSize: Long?): Completable
    fun fetchAppInitBool(): Boolean
    fun pushAppInitBool(boolean: Boolean = true): Completable
    val accounts: Observable<List<Account>>
    fun push(account: Account): Completable
    fun update(account: Account): Completable
    fun delete(account: Account): Completable
}
