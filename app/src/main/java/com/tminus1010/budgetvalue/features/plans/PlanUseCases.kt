package com.tminus1010.budgetvalue.features.plans

import com.tminus1010.budgetvalue.features.categories.Category
import com.tminus1010.budgetvalue.features.reconciliations.Reconciliation
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

interface PlanUseCases {
    val plans: Observable<List<Plan>>
    fun pushPlan(plan: Plan): Completable
    fun pushPlanCA(plan: Plan, category: Category, amount: BigDecimal?): Completable
    val activePlanCAs: Observable<Map<Category, BigDecimal>>
    fun pushActivePlanCAs(categoryAmounts: Map<Category, BigDecimal>): Completable
    fun pushActivePlanCA(kv: Pair<Category, BigDecimal?>): Completable
    fun clearActivePlan(): Completable
}