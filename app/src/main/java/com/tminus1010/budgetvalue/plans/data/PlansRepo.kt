package com.tminus1010.budgetvalue.plans.data

import com.tminus1010.budgetvalue._core.data.MiscDAO
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.models.Plan
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import javax.inject.Inject

class PlansRepo @Inject constructor(
    private val miscDAO: MiscDAO,
    private val categoryAmountsConverter: CategoryAmountsConverter
) {
    val plans: Observable<List<Plan>> =
        miscDAO.fetchPlans().subscribeOn(Schedulers.io())
            .map { it.map { Plan.fromDTO(it, categoryAmountsConverter) } }

    fun pushPlan(plan: Plan): Completable =
        miscDAO.add(plan.toDTO(categoryAmountsConverter)).subscribeOn(Schedulers.io())

    fun updatePlanCA(plan: Plan, category: Category, amount: BigDecimal): Completable =
        plan.categoryAmounts
            .toMutableMap()
            .apply { if (amount.compareTo(BigDecimal.ZERO) == 0) remove(category) else put(category, amount) }
            .let {
                miscDAO.updatePlanCategoryAmounts(
                    plan.toDTO(categoryAmountsConverter).startDate,
                    it.mapKeys { it.key.name }).subscribeOn(Schedulers.io())
            }

    fun updatePlanAmount(plan: Plan, amount: BigDecimal): Completable =
        miscDAO.updatePlanAmount(plan.toDTO(categoryAmountsConverter).startDate, amount).subscribeOn(Schedulers.io())

    fun delete(plan: Plan): Completable =
        miscDAO.delete(plan.toDTO(categoryAmountsConverter))
            .subscribeOn(Schedulers.io())
}