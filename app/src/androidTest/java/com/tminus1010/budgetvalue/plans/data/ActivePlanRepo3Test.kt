package com.tminus1010.budgetvalue.plans.data

import com.tminus1010.budgetvalue.Given
import com.tminus1010.budgetvalue._core.domain.CategoryAmounts
import com.tminus1010.budgetvalue._core.domain.DatePeriodService
import com.tminus1010.budgetvalue.plans.domain.Plan
import com.tminus1010.tmcommonkotlin.core.logx
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
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal("0"),
                CategoryAmounts(),
            ),
            activePlanRepo.activePlan.value
        )
        Thread.sleep(500) // Why is this necessary..?
    }

    @Test
    fun clearCategoryAmounts() = runBlocking {
        // # Given
        activePlanRepo.updateCategoryAmount(Given.categories[0], BigDecimal("9"))
        Thread.sleep(500) // Why is this necessary..?
        // # When
        activePlanRepo.clearCategoryAmounts()
        Thread.sleep(500) // Why is this necessary..?
        // # Then
        assertEquals(
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal("0"),
                CategoryAmounts(),
            ),
            activePlanRepo.activePlan.value,
        )
        Thread.sleep(500) // Why is this necessary..?
    }

    @Test
    fun updateCategoryAmount() = runBlocking {
        // # When
        activePlanRepo.updateCategoryAmount(Given.categories[0], BigDecimal("22"))
        Thread.sleep(500) // Why is this necessary..?
        // # Then
        assertEquals(
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal("0"),
                CategoryAmounts(Given.categories[0] to BigDecimal("22")),
            ),
            activePlanRepo.activePlan.value.logx("valueOfTest"),
        )
        Thread.sleep(500) // Why is this necessary..?
    }

    @Test
    fun updateTotal() = runBlocking {
        // # When
        activePlanRepo.updateTotal(BigDecimal("98"))
        Thread.sleep(500) // Why is this necessary..?
        // # Then
        assertEquals(
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal("98"),
                CategoryAmounts(),
            ),
            activePlanRepo.activePlan.value,
        )
        Thread.sleep(500) // Why is this necessary..?
    }
}