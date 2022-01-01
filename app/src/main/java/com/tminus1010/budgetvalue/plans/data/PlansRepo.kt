package com.tminus1010.budgetvalue.plans.data

import com.tminus1010.budgetvalue._core.all.extensions.isZero
import com.tminus1010.budgetvalue._core.data.MiscDatabase
import com.tminus1010.budgetvalue._core.domain.CategoryAmounts
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.domain.Plan
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.math.BigDecimal
import javax.inject.Inject

class PlansRepo @Inject constructor(
    miscDatabase: MiscDatabase,
) {
    private val miscDAO = miscDatabase.miscDAO()
    val plans =
        miscDAO.getPlans()
            .stateIn(GlobalScope, SharingStarted.Eagerly, null)

    suspend fun push(plan: Plan) = miscDAO.insert(plan)
    suspend fun updatePlanAmount(plan: Plan, amount: BigDecimal) = miscDAO.updatePlanAmount(plan.localDatePeriod, amount)
    suspend fun updatePlan(plan: Plan) = miscDAO.update(plan)
    suspend fun delete(plan: Plan) = miscDAO.delete(plan)
    suspend fun updatePlanCategoryAmount(plan: Plan, category: Category, amount: BigDecimal) {
        val planRedefined = miscDAO.getPlan(plan.localDatePeriod) ?: return
        val categoryAmounts =
            planRedefined.categoryAmounts
                .toMutableMap()
                .also { if (amount.isZero) it.remove(category) else it[category] = amount }
                .let { CategoryAmounts(it) }
        miscDAO.updatePlanCategoryAmounts(plan.localDatePeriod, categoryAmounts)
    }
}