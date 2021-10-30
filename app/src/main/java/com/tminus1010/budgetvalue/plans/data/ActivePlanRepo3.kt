package com.tminus1010.budgetvalue.plans.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue._core.app.DatePeriodService
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
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class ActivePlanRepo3 @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val moshi: Moshi,
    private val categoryAmountsConverter: CategoryAmountsConverter,
    datePeriodService: DatePeriodService
) {
    private val key = stringPreferencesKey("ActivePlanRepo3")
    private val default =
        Plan(
            datePeriodService.getDatePeriod(LocalDate.now()),
            BigDecimal.ZERO,
            mapOf()
        )

    suspend fun update(plan: Plan) {
        logz("update plan:$plan")
        dataStore.edit { it[key] = moshi.toJson(plan.toDTO(categoryAmountsConverter)).also { logz("qqq:$it") } }
    }

    suspend fun update(planTransformation: (Plan) -> Plan) {
        update(planTransformation(activePlan.value))
    }

    suspend fun clearCategoryAmounts() {
        update { it.copy(categoryAmounts = mapOf()) }
    }

    val activePlan =
        dataStore.data
            .map {
                moshi.fromJson<PlanDTO>(it[key].also { logz("bbb:$it") })
                    ?.let { Plan.fromDTO(it, categoryAmountsConverter) }
                    .also { logz("aaa:$it") }
                    ?: default
            }
            .distinctUntilChanged()
            .stateIn(GlobalScope, SharingStarted.Eagerly, default)
}