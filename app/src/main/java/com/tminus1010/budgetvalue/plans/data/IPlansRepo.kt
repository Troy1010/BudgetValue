package com.tminus1010.budgetvalue.plans.data

import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.plans.models.Plan
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

interface IPlansRepo {
    val plans: Observable<List<Plan>> // includes active plan
    fun pushPlan(plan: Plan): Completable
    fun updatePlanCA(plan: Plan, category: Category, amount: BigDecimal?): Completable
    fun updatePlanCAs(plan: Plan, categoryAmounts: Map<String, BigDecimal>): Completable
    fun updatePlanAmount(plan: Plan, amount: BigDecimal): Completable
    fun delete(plan: Plan): Completable
}