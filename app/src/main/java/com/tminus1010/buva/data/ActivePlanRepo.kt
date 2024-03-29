package com.tminus1010.buva.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tminus1010.buva.all_layers.extensions.easyShareIn
import com.tminus1010.buva.all_layers.extensions.isZero
import com.tminus1010.buva.domain.ActivePlan
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.buva.environment.adapter.MoshiWithCategoriesProvider
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivePlanRepo @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val moshiWithCategoriesProvider: MoshiWithCategoriesProvider,
) {
    private val key = stringPreferencesKey("ActivePlanRepo3")
    private val defaultValue =
        ActivePlan(
            total = BigDecimal.ZERO,
            categoryAmounts = CategoryAmounts(),
        )

    val activePlan =
        combine(moshiWithCategoriesProvider.moshiFlow, dataStore.data)
        { moshi, data ->
            moshi.fromJson<ActivePlan>(data[key])
        }
            .easyShareIn(GlobalScope, SharingStarted.Eagerly, defaultValue)

    private suspend fun push(activePlan: ActivePlan?) {
        if (activePlan == null)
            dataStore.edit { it.remove(key) }
        else
            dataStore.edit { it[key] = moshiWithCategoriesProvider.moshiFlow.first().toJson(activePlan) }
    }

    suspend fun clearCategoryAmounts() {
        push(activePlan.first().copy(categoryAmounts = CategoryAmounts()))
    }

    suspend fun updateTotal(total: BigDecimal) {
        push(activePlan.first().copy(total = total))
    }

    suspend fun pushCategoryAmounts(categoryAmounts: CategoryAmounts) {
        push(activePlan.first().copy(categoryAmounts = categoryAmounts))
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