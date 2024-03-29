package com.tminus1010.buva.data

import com.tminus1010.buva.all_layers.extensions.isZero
import com.tminus1010.buva.all_layers.extensions.redoWhen
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.buva.domain.Plan
import com.tminus1010.buva.environment.adapter.MoshiWithCategoriesProvider
import com.tminus1010.buva.environment.room.MiscDAO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import java.math.BigDecimal
import javax.inject.Inject

// TODO: Atm, there are only ActivePlan and Reconciliation.. maybe this can be deleted?
class PlansRepo @Inject constructor(
    private val moshiWithCategoriesProvider: MoshiWithCategoriesProvider,
    private val miscDAO: MiscDAO,
) {
    val plans =
        miscDAO.fetchPlans()
            .redoWhen(moshiWithCategoriesProvider.moshiFlow) // Room synchronously depends on moshiWithCategories, so we must redo when it emits.
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    suspend fun push(plan: Plan) =
        miscDAO.insert(plan)

    suspend fun updatePlanAmount(plan: Plan, amount: BigDecimal) =
        miscDAO.updatePlanAmount(plan.localDatePeriod, amount)

    suspend fun updatePlan(plan: Plan) =
        miscDAO.update(plan)

    suspend fun delete(plan: Plan) =
        miscDAO.delete(plan)

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