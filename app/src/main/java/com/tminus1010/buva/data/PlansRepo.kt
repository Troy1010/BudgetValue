package com.tminus1010.buva.data

import com.tminus1010.buva.all_layers.extensions.isZero
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.buva.domain.Plan
import com.tminus1010.buva.environment.database_or_datastore_or_similar.MiscDAO
import com.tminus1010.buva.environment.database_or_datastore_or_similar.UserCategoriesDAO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import java.math.BigDecimal
import javax.inject.Inject

// TODO: Atm, there are only ActivePlan and Reconciliation.. maybe this can be deleted?
class PlansRepo @Inject constructor(
    private val userCategoriesDAO: UserCategoriesDAO,
    private val miscDAO: MiscDAO,
) {
    val plans =
        userCategoriesDAO.fetchUserCategories().flatMapLatest {
            miscDAO.fetchPlans()
        }
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