package com.tminus1010.budgetvalue.plans.data

import com.tminus1010.budgetvalue.Given
import com.tminus1010.budgetvalue._core.domain.CategoryAmounts
import com.tminus1010.budgetvalue._core.domain.DatePeriodService
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
class PlansRepo2Test {
    @get:Rule
    val hiltAndroidRule = HiltAndroidRule(this)

    @Inject
    lateinit var datePeriodService: DatePeriodService

    @Inject
    lateinit var plansRepo: PlansRepo2

    @Before
    fun before() {
        hiltAndroidRule.inject()
    }

    // # Tests

    @Test
    fun default() = runBlocking {
        // # When
        val result = plansRepo.plans.value
        // # Then
        assertEquals(null, result)
    }

    @Test
    fun push() = runBlocking {
        // # Given
        val givenPlan =
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9"))
            )
        // # When
        plansRepo.push(givenPlan)
        Thread.sleep(1000)
        // # Then
        assertEquals(listOf(givenPlan), plansRepo.plans.value)
    }

    @Test
    fun updatePlanCategoryAmount() = runBlocking {
        // # Given
        val givenPlan =
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9"))
            )
        plansRepo.push(givenPlan)
        Thread.sleep(1000)
        // # When
        plansRepo.updatePlanCategoryAmount(givenPlan, Given.categories[0], BigDecimal("35"))
        Thread.sleep(1000)
        // # Then
        assertEquals(
            listOf(
                Plan(
                    datePeriodService.getDatePeriod(LocalDate.now()),
                    BigDecimal("11"),
                    CategoryAmounts(Given.categories[0] to BigDecimal("35"))
                )
            ),
            plansRepo.plans.value,
        )
    }

    @Test
    fun updatePlanAmount() = runBlocking {
        // # Given
        val givenPlan =
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9"))
            )
        plansRepo.push(givenPlan)
        Thread.sleep(1000)
        // # When
        plansRepo.updatePlanAmount(givenPlan, BigDecimal("77"))
        Thread.sleep(1000)
        // # Then
        assertEquals(
            listOf(
                Plan(
                    datePeriodService.getDatePeriod(LocalDate.now()),
                    BigDecimal("77"),
                    CategoryAmounts(Given.categories[0] to BigDecimal("9"))
                )
            ),
            plansRepo.plans.value,
        )
    }

    @Test
    fun updatePlan() = runBlocking {
        // # Given
        val givenPlan =
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9"))
            )
        plansRepo.push(givenPlan)
        Thread.sleep(1000)
        val givenPlan2 =
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9"))
            )
        // # When
        plansRepo.updatePlan(givenPlan2)
        Thread.sleep(1000)
        // # Then
        assertEquals(listOf(givenPlan2), plansRepo.plans.value)
    }

    @Test
    fun delete() = runBlocking {
        // # Given
        val givenPlan =
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9"))
            )
        plansRepo.push(givenPlan)
        Thread.sleep(1000)
        // # When
        plansRepo.delete(givenPlan)
        Thread.sleep(1000)
        // # Then
        assertEquals(listOf<Plan>(), plansRepo.plans.value)
    }
}