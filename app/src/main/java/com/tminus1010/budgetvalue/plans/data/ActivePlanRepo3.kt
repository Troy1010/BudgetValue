package com.tminus1010.budgetvalue.plans.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tminus1010.budgetvalue._core.data.MoshiWithCategoriesProvider
import com.tminus1010.budgetvalue.plans.domain.Plan
import com.tminus1010.tmcommonkotlin.core.logx
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

// TODO: This Repo may no longer fit the requirements
@Singleton
class ActivePlanRepo3 @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val moshiWithCategoriesProvider: MoshiWithCategoriesProvider
) {
    private val key = stringPreferencesKey("ActivePlanRepo3")

    suspend fun push(plan: Plan?) {
        if (plan == null)
            dataStore.edit { it.remove(key) }
        else
            dataStore.edit { it[key] = moshiWithCategoriesProvider.moshi.toJson(plan).logx("toJson") }
    }

    suspend fun clearCategoryAmounts() {
        push(activePlan.value?.copy(categoryAmounts = mapOf()))
    }

    val activePlan =
        dataStore.data
            .map {
                try {
                    moshiWithCategoriesProvider.moshi.fromJson<Plan>(it[key].logx("for fromJson")).logx("fromJson")
                } catch (e: Throwable) {
                    logz("someError, oh no!", e)
                    throw e
                }
            }
            .onCompletion { if (it != null) logz("erro4!", it) }
            .distinctUntilChanged()
            .stateIn(GlobalScope, SharingStarted.Eagerly, null)
}