package com.tminus1010.budgetvalue.plans.data

import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue.FakeDatastore
import com.tminus1010.budgetvalue.Given
import com.tminus1010.budgetvalue._core.app.DatePeriodService
import com.tminus1010.budgetvalue._core.data.repos.SettingsRepo
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.plans.domain.Plan
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@HiltAndroidTest
class ActivePlanRepo3Test {
    @get:Rule
    val hiltAndroidRule = HiltAndroidRule(this)

    @Inject
    lateinit var moshi: Moshi

    @Inject
    lateinit var categoryAmountsConverter: CategoryAmountsConverter

    @Inject
    lateinit var datePeriodService: DatePeriodService

    @Test
    fun anchorDateOffset_default_push() {
        // # Given
        hiltAndroidRule.inject()
        val activePlanRepo =
            ActivePlanRepo3(
                FakeDatastore(),
                moshi,
                categoryAmountsConverter,
                datePeriodService
            )
        // # Then
        assertEquals(
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal.ZERO,
                mapOf()
            ),
            activePlanRepo.activePlan.value
        )
        // # When
        runBlocking {
            activePlanRepo.update(
                Plan(
                    datePeriodService.getDatePeriod(LocalDate.now()),
                    BigDecimal.TEN,
                    mapOf()
                )
            )
        }
        Thread.sleep(1000)
        // # Then
        assertEquals(
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal.TEN,
                mapOf()
            ),
            activePlanRepo.activePlan.value
        )
    }

//    @Test
//    fun default_update_clearCategoryAmounts() {
//        // # Given
//        hiltAndroidRule.inject()
//        val activePlanRepo3 =
//            ActivePlanRepo3(
//                FakeDatastore(),
//                moshi,
//                categoryAmountsConverter,
//                datePeriodService,
//            )
//        // # Then
//        assertEquals(
//            Plan(
//                datePeriodService.getDatePeriod(LocalDate.now()),
//                BigDecimal.ZERO,
//                mapOf()
//            ),
//            activePlanRepo3.activePlan.value
//        )
//        // # Given
//        val givenPlan =
//            Plan(
//                datePeriodService.getDatePeriod(LocalDate.now()),
//                BigDecimal("11"),
//                mapOf(Given.categories[0] to BigDecimal("8"))
//            )
//        // # When
//        runBlocking(Dispatchers.IO) { activePlanRepo3.update(givenPlan) }
//        Thread.sleep(1000)
//        // # Then
//        assertEquals(
//            givenPlan,
//            activePlanRepo3.activePlan.value
//        )
//        // # When
//        runBlocking { activePlanRepo3.clearCategoryAmounts() }
//        Thread.sleep(1000)
//        // # Then
//        assertEquals(
//            Plan(
//                datePeriodService.getDatePeriod(LocalDate.now()),
//                BigDecimal("11"),
//                mapOf()
//            ),
//            activePlanRepo3.activePlan.value
//        )
//    }
}