package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.model_app.Plan
import com.tminus1010.budgetvalue.model_app.Reconciliation
import com.tminus1010.budgetvalue.model_app.Transaction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface IMyDaoWrapper: MyDao {
    val transactions: Observable<List<Transaction>>
    fun pushReconciliation(reconciliation: Reconciliation): Completable
    fun fetchReconciliations(): Observable<List<Reconciliation>>
    val plans: Observable<List<Plan>>
    fun pushPlan(plan: Plan)
}