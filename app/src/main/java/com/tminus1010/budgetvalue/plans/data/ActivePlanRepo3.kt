package com.tminus1010.budgetvalue.plans.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.plans.data.model.PlanDTO
import com.tminus1010.budgetvalue.plans.domain.Plan
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class ActivePlanRepo3 @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val moshi: Moshi,
    private val categoryAmountsConverter: CategoryAmountsConverter,
) {
    private val key = stringPreferencesKey("ActivePlanRepo3")

    suspend fun push(plan: Plan?) {
        if (plan == null)
            dataStore.edit { it.remove(key) }
        else
            dataStore.edit { it[key] = moshi.toJson(plan.toDTO(categoryAmountsConverter)) }
    }

    suspend fun clearCategoryAmounts() {
        push(activePlan.value?.copy(categoryAmounts = mapOf()))
    }

    val activePlan =
        dataStore.data
            .map { moshi.fromJson<PlanDTO>(it[key])?.let { Plan.fromDTO(it, categoryAmountsConverter) } }
            .distinctUntilChanged()
            .stateIn(GlobalScope, SharingStarted.Eagerly, null)
}