package com.tminus1010.budgetvalue.plans.data

import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue.FakeDatastore
import com.tminus1010.budgetvalue._core.app.DatePeriodService
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.plans.domain.Plan
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidTest
class ActivePlanRepo2Test {
    @get:Rule
    val hiltAndroidRule = HiltAndroidRule(this)

    @Inject
    lateinit var moshi: Moshi

    @Inject
    lateinit var categoryAmountsConverter: CategoryAmountsConverter

    @Inject
    lateinit var datePeriodService: DatePeriodService


    @Test
    fun defaultPlan_update_clear() {
        // # Given
        hiltAndroidRule.inject()
        val activePlanRepo =
            ActivePlanRepo2(
                dataStore = FakeDatastore(),
                moshi = moshi,
                categoryAmountsConverter = categoryAmountsConverter,
                datePeriodService = datePeriodService,
            )
        // # When
        activePlanRepo.activePlan
            .throttleLast(500, TimeUnit.MILLISECONDS)
            .timeout(3, TimeUnit.SECONDS)
            .take(1)
            .test()
            .await()
            .assertValue { it == Plan(datePeriodService.getDatePeriod(LocalDate.now()), BigDecimal.ZERO, mapOf()) }
    }
}