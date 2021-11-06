package com.tminus1010.budgetvalue.plans.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tminus1010.budgetvalue._core.all.extensions.isZero
import com.tminus1010.budgetvalue._core.data.MoshiWithCategoriesProvider
import com.tminus1010.budgetvalue._core.domain.CategoryAmounts
import com.tminus1010.budgetvalue._core.domain.DatePeriodService
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.domain.Plan
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivePlanRepo3 @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val moshiWithCategoriesProvider: MoshiWithCategoriesProvider,
    datePeriodService: DatePeriodService
) {
    private val key = stringPreferencesKey("ActivePlanRepo3")

    val activePlan =
        dataStore.data
            .map { moshiWithCategoriesProvider.moshi.fromJson<Plan>(it[key]) }
            .filterNotNull()
            .distinctUntilChanged()
            .stateIn(
                GlobalScope,
                SharingStarted.Eagerly,
                Plan(
                    datePeriodService.getDatePeriod(LocalDate.now()), // TODO: Make an ActivePlan class (of a sealed class), which does not need a localDatePeriod.
                    total = BigDecimal.ZERO,
                    categoryAmounts = CategoryAmounts(),
                )
            )

    private suspend fun push(plan: Plan?) {
        if (plan == null)
            dataStore.edit { it.remove(key) }
        else
            dataStore.edit { it[key] = moshiWithCategoriesProvider.moshi.toJson(plan) }
    }

    suspend fun clearCategoryAmounts() {
        push(activePlan.first().copy(categoryAmounts = CategoryAmounts()))
    }

    suspend fun updateTotal(total: BigDecimal) {
        push(activePlan.first().copy(total = total))
    }

    suspend fun updateCategoryAmount(category: Category, amount: BigDecimal) {
        val oldActivePlan = activePlan.first()
        val categoryAmounts =
            oldActivePlan.categoryAmounts
                .toMutableMap()
                .also { if (amount.isZero) it.remove(category) else it[category] = amount }
                .let { CategoryAmounts(it) }
        push(oldActivePlan.copy(categoryAmounts = categoryAmounts))
    }
}