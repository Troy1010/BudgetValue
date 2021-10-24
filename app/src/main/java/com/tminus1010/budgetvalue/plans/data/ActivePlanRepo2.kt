package com.tminus1010.budgetvalue.plans.data

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue._core.all.extensions.mapNotNull
import com.tminus1010.budgetvalue._core.app.DatePeriodService
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
import kotlinx.coroutines.sync.Semaphore
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class ActivePlanRepo2 constructor(
    private val dataStore: DataStore<Preferences>,
    private val moshi: Moshi,
    private val categoryAmountsConverter: CategoryAmountsConverter,
    private val datePeriodService: DatePeriodService
) {
    @Inject
    constructor(app: Application, moshi: Moshi, categoryAmountsConverter: CategoryAmountsConverter, datePeriodService: DatePeriodService) : this(app.dataStore, moshi, categoryAmountsConverter, datePeriodService)

    private val key = stringPreferencesKey("ActivePlanRepo2")
    private val semaphore = Semaphore(1)

    fun update(plan: Plan) {
        GlobalScope.launch { semaphore.acquire(); dataStore.edit { it[key] = moshi.toJson(plan.toDTO(categoryAmountsConverter)) }; semaphore.release() }
    }

    fun update(planTransformation: (Plan) -> Plan) {
        activePlan.take(1).subscribe { update(planTransformation(it)) }
    }

    fun clearCategoryAmounts() {
        update { it.copy(categoryAmounts = mapOf()) }
    }

    val activePlan =
        dataStore.data.asObservable()
            .mapNotNull { moshi.fromJson<PlanDTO>(it[key])?.let { Plan.fromDTO(it, categoryAmountsConverter) } }
            .distinctUntilChanged()
            .replay(1).autoConnect()

    init {
        if (activePlan.value == null)
            update(
                Plan(
                    datePeriodService.getDatePeriod(LocalDate.now()),
                    BigDecimal.ZERO,
                    mapOf()
                )
            )
    }
}