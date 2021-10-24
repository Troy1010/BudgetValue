package com.tminus1010.budgetvalue.plans.data

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue._core.all.extensions.mapNotNull
import com.tminus1010.budgetvalue._core.app.LocalDatePeriod
import com.tminus1010.budgetvalue._core.data.dataStore
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.plans.data.model.PlanDTO
import com.tminus1010.budgetvalue.plans.domain.Plan
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.asObservable
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class ActivePlanRepo2 constructor(
    private val dataStore: DataStore<Preferences>,
    private val moshi: Moshi,
    private val categoryAmountsConverter: CategoryAmountsConverter,
) {
    @Inject
    constructor(app: Application, moshi: Moshi, categoryAmountsConverter: CategoryAmountsConverter) : this(app.dataStore, moshi, categoryAmountsConverter)

    private val key = stringPreferencesKey("ActivePlanRepo2")
    fun create() {
        update(
            Plan(
                LocalDatePeriod(
                    LocalDate.now().minusDays(7),
                    LocalDate.now(),
                ),
                BigDecimal.ZERO,
                mapOf()
            )
        )
    }

    fun update(plan: Plan) {
        GlobalScope.launch { dataStore.edit { it[key] = moshi.toJson(plan.toDTO(categoryAmountsConverter)) } }
    }

    fun clearCategoryAmounts() {
        activePlan.value
            ?.also { update(it.copy(categoryAmounts = mapOf())) }
    }

    val activePlan =
        dataStore.data.asObservable()
            .mapNotNull { moshi.fromJson<PlanDTO>(it[key])?.let { Plan.fromDTO(it, categoryAmountsConverter) } }
            .distinctUntilChanged()
            .replay(1).autoConnect()
}