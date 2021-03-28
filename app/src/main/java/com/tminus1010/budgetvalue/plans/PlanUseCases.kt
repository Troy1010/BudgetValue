package com.tminus1010.budgetvalue.plans

import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.plans.models.Plan
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

interface PlanUseCases {
    val plans: Observable<List<Plan>> // includes active plan
    fun pushPlan(plan: Plan): Completable
    fun updatePlanCA(plan: Plan, category: Category, amount: BigDecimal?): Completable
    fun updatePlanAmount(plan: Plan, amount: BigDecimal): Completable
    fun delete(plan: Plan): Completable
}