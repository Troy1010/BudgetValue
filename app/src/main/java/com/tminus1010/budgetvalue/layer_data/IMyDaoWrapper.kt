package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.model_data.Category
import com.tminus1010.budgetvalue.model_app.Plan
import com.tminus1010.budgetvalue.model_app.Reconciliation
import com.tminus1010.budgetvalue.model_app.Transaction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

interface IMyDaoWrapper: MyDao {
    val transactions: Observable<List<Transaction>>
    fun pushTransactionCA(transaction: Transaction, category: Category, amount: BigDecimal?)
    val reconciliations: Observable<List<Reconciliation>>
    fun pushReconciliation(reconciliation: Reconciliation): Completable
    fun pushReconciliationCA(reconciliation: Reconciliation, category: Category, amount: BigDecimal?)
    val plans: Observable<List<Plan>>
    fun pushPlan(plan: Plan): Completable
    fun pushPlanCA(plan: Plan, category: Category, amount: BigDecimal?)
}