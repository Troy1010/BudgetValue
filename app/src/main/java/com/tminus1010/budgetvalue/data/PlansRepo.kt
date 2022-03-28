package com.tminus1010.budgetvalue.data

import com.tminus1010.budgetvalue.all_layers.extensions.isZero
import com.tminus1010.budgetvalue.data.service.MiscDatabase
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.domain.plan.Plan
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import java.math.BigDecimal
import javax.inject.Inject

class PlansRepo @Inject constructor(
    miscDatabase: MiscDatabase,
) {
    private val miscDAO = miscDatabase.miscDAO()
    val plans = miscDAO.getPlans().shareIn(GlobalScope, SharingStarted.Eagerly, 1)
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