package com.tminus1010.budgetvalue.plans.data

import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue.FakeDatastore
import com.tminus1010.budgetvalue.Given
import com.tminus1010.budgetvalue._core.app.DatePeriodService
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.plans.domain.Plan
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
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
    lateinit var moshi: Moshi

    @Inject
    lateinit var datePeriodService: DatePeriodService

    lateinit var categoryAmountsConverter: CategoryAmountsConverter
    lateinit var activePlanRepo: ActivePlanRepo3

    @Before
    fun before() {
        hiltAndroidRule.inject()
        categoryAmountsConverter =
            CategoryAmountsConverter(
                CategoriesInteractor(
                    mockk {
                        every { userCategories } returns
                                flow { emit(listOf()); emit(Given.categories) }
                    }
                ),
                moshi
            )
        activePlanRepo =
            ActivePlanRepo3(
                FakeDatastore(),
                moshi,
                categoryAmountsConverter,
                datePeriodService
            )
    }

    @Test
    fun default() = runBlocking {
        // # Then
        assertEquals(
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal.ZERO,
                mapOf()
            ),
            activePlanRepo.activePlan.value
        )
    }

    @Test
    fun update1() = runBlocking {
        // # Given
        val givenNewPlan =
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal("11"),
                mapOf(Given.categories[0] to BigDecimal("9"))
            )
        // # When
        activePlanRepo.update(givenNewPlan)
        Thread.sleep(500) // Why is this necessary..?
        // # Then
        assertEquals(givenNewPlan, activePlanRepo.activePlan.value)
    }

    @Test
    fun update2() = runBlocking {
        // # Given
        val givenNewAmount = BigDecimal("17")
        // # When
        activePlanRepo.update { it.copy(amount = givenNewAmount) }
        Thread.sleep(500) // Why is this necessary..?
        // # Then
        assertEquals(givenNewAmount, activePlanRepo.activePlan.value.amount)
    }

    @Test
    fun clearCategoryAmounts() = runBlocking {
        // # Given
        val givenNewPlan =
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal("11"),
                mapOf(Given.categories[0] to BigDecimal("9"))
            )
        activePlanRepo.update(givenNewPlan)
        Thread.sleep(500) // Why is this necessary..?
        // # When
        activePlanRepo.clearCategoryAmounts()
        Thread.sleep(500) // Why is this necessary..?
        // # Then
        assertEquals(
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal("11"),
                mapOf()
            ),
            activePlanRepo.activePlan.value
        )
    }
}