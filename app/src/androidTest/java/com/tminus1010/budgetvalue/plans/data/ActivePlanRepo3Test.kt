package com.tminus1010.budgetvalue.plans.data

import com.tminus1010.budgetvalue.Given
import com.tminus1010.budgetvalue._core.app.CategoryAmounts
import com.tminus1010.budgetvalue._core.app.DatePeriodService
import com.tminus1010.budgetvalue.plans.domain.Plan
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
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
    lateinit var datePeriodService: DatePeriodService

    @Inject
    lateinit var activePlanRepo: ActivePlanRepo3

    @Before
    fun before() {
        hiltAndroidRule.inject()
    }

    @Test
    fun default() = runBlocking {
        // # Then
        assertEquals(
            null,
            activePlanRepo.activePlan.value
        )
    }

    @Test
    fun push() = runBlocking {
        // # Given
        val givenNewPlan =
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9"))
            )
        // # When
        activePlanRepo.push(givenNewPlan)
        Thread.sleep(500) // Why is this necessary..?
        // # Then
        assertEquals(givenNewPlan, activePlanRepo.activePlan.value)
        Thread.sleep(500) // Why is this necessary..?
    }

    @Test
    fun clearCategoryAmounts() = runBlocking {
        // # Given
        val givenNewPlan =
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9"))
            )
        activePlanRepo.push(givenNewPlan)
        Thread.sleep(500) // Why is this necessary..?
        // # When
        activePlanRepo.clearCategoryAmounts()
        Thread.sleep(500) // Why is this necessary..?
        // # Then
        assertEquals(
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal("11"),
                CategoryAmounts()
            ),
            activePlanRepo.activePlan.value,
        )
        Thread.sleep(500) // Why is this necessary..?
    }
}