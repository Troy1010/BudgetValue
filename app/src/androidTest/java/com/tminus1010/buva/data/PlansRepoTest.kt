package com.tminus1010.buva.data

import androidx.test.core.app.ApplicationProvider
import com.tminus1010.buva.all_layers.DaggerAppComponent
import com.tminus1010.buva.app.DatePeriodService
import com.tminus1010.buva.core_testing.BaseFakeEnvironmentModule
import com.tminus1010.buva.core_testing.shared.Given
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.buva.domain.Plan
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class PlansRepoTest {
    @Test
    fun default() = runBlocking {
        // # Given
        Given.categories.forEach { categoryRepo.push(it) }
        // # When
        val result = plansRepo.plans.first()
        // # Then
        assertEquals(listOf<Plan>(), result)
    }

    @Test
    fun push() = runBlocking {
        // # Given
        Given.categories.forEach { categoryRepo.push(it) }
        val givenPlan =
            Plan(
                datePeriodService.getDatePeriod2(LocalDate.now()).first(),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9")),
            )
        // # When
        plansRepo.push(givenPlan)
        Thread.sleep(1000)
        // # Then
        assertEquals(listOf(givenPlan), plansRepo.plans.first())
    }

    @Test
    fun updatePlanCategoryAmount() = runBlocking {
        // # Given
        Given.categories.forEach { categoryRepo.push(it) }
        val givenPlan =
            Plan(
                datePeriodService.getDatePeriod2(LocalDate.now()).first(),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9")),
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
                    datePeriodService.getDatePeriod2(LocalDate.now()).first(),
                    BigDecimal("11"),
                    CategoryAmounts(Given.categories[0] to BigDecimal("35")),
                ),
            ),
            plansRepo.plans.first(),
        )
    }

    @Test
    fun updatePlanAmount() = runBlocking {
        // # Given
        Given.categories.forEach { categoryRepo.push(it) }
        val givenPlan =
            Plan(
                datePeriodService.getDatePeriod2(LocalDate.now()).first(),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9")),
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
                    datePeriodService.getDatePeriod2(LocalDate.now()).first(),
                    BigDecimal("77"),
                    CategoryAmounts(Given.categories[0] to BigDecimal("9")),
                ),
            ),
            plansRepo.plans.first(),
        )
    }

    @Test
    fun updatePlan() = runBlocking {
        // # Given
        Given.categories.forEach { categoryRepo.push(it) }
        val givenPlan =
            Plan(
                datePeriodService.getDatePeriod2(LocalDate.now()).first(),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9")),
            )
        plansRepo.push(givenPlan)
        Thread.sleep(1000)
        val givenPlan2 =
            Plan(
                datePeriodService.getDatePeriod2(LocalDate.now()).first(),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9")),
            )
        // # When
        plansRepo.updatePlan(givenPlan2)
        Thread.sleep(1000)
        // # Then
        assertEquals(listOf(givenPlan2), plansRepo.plans.first())
    }

    @Test
    fun delete() = runBlocking {
        // # Given
        Given.categories.forEach { categoryRepo.push(it) }
        val givenPlan =
            Plan(
                datePeriodService.getDatePeriod2(LocalDate.now()).first(),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9")),
            )
        plansRepo.push(givenPlan)
        Thread.sleep(1000)
        // # When
        plansRepo.delete(givenPlan)
        Thread.sleep(1000)
        // # Then
        assertEquals(listOf<Plan>(), plansRepo.plans.first())
    }

    lateinit var plansRepo: PlansRepo
    lateinit var categoryRepo: CategoryRepo
    lateinit var datePeriodService: DatePeriodService

    @Before
    fun before() {
        val component =
            DaggerAppComponent.builder()
                .environmentModule(BaseFakeEnvironmentModule())
                .application(ApplicationProvider.getApplicationContext())
                .build()
        plansRepo = component.plansRepo()
        categoryRepo = component.categoryRepo()
        datePeriodService = component.datePeriodService()
    }
}