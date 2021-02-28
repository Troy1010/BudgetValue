package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.model_data.AccountDTO
import com.tminus1010.budgetvalue.model_data.Category
import com.tminus1010.budgetvalue.model_domain.Plan
import com.tminus1010.budgetvalue.model_domain.Reconciliation
import com.tminus1010.budgetvalue.model_domain.Transaction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

interface IRepoWrapper {
    val transactions: Observable<List<Transaction>>
    fun pushTransactionCA(transaction: Transaction, category: Category, amount: BigDecimal?)
    val plans: Observable<List<Plan>>
    fun pushPlan(plan: Plan): Completable
    fun pushPlanCA(plan: Plan, category: Category, amount: BigDecimal?)
    fun pushReconciliation(reconciliation: Reconciliation): Completable
    val reconciliations: Observable<List<Reconciliation>>
    fun pushReconciliationCA(reconciliation: Reconciliation, category: Category, amount: BigDecimal?)
    fun update(accountDTO: AccountDTO): Completable
}
