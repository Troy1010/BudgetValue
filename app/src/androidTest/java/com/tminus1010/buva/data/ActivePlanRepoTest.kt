package com.tminus1010.buva.data

import androidx.test.core.app.ApplicationProvider
import com.tminus1010.buva.all_layers.DaggerAppComponent
import com.tminus1010.buva.core_testing.BaseFakeEnvironmentModule
import com.tminus1010.buva.core_testing.shared.Given
import com.tminus1010.buva.domain.ActivePlan
import com.tminus1010.buva.domain.CategoryAmounts
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class ActivePlanRepoTest {

    @Test
    fun default() = runBlocking {
        // # Given
        val expected =
            ActivePlan(
                BigDecimal("0"),
                CategoryAmounts(),
            )
        // # When
        // # Then
        assertEquals(expected, activePlanRepo.activePlan.first())
    }

    @Test
    fun clearCategoryAmounts() = runBlocking {
        // # Given
        val expected =
            ActivePlan(
                BigDecimal("0"),
                CategoryAmounts(),
            )
        Given.categories.forEach { categoryRepo.push(it) }
        activePlanRepo.updateCategoryAmount(Given.categories[0], BigDecimal("9"))
        Thread.sleep(500) // Why is this necessary..?
        // # When
        activePlanRepo.clearCategoryAmounts()
        Thread.sleep(500) // Why is this necessary..?
        // # Then
        assertEquals(expected, activePlanRepo.activePlan.first())
    }

    @Test
    fun updateCategoryAmount() = runBlocking {
        // # Given
        val expected =
            ActivePlan(
                BigDecimal("0"),
                CategoryAmounts(Given.categories[0] to BigDecimal("22")),
            )
        Given.categories.forEach { categoryRepo.push(it) }
        // # When
        activePlanRepo.updateCategoryAmount(Given.categories[0], BigDecimal("22"))
        Thread.sleep(500) // Why is this necessary..?
        // # Then
        assertEquals(expected, activePlanRepo.activePlan.first())
    }

    @Test
    fun updateTotal() = runBlocking {
        // # Given
        val expected =
            ActivePlan(
                BigDecimal("98"),
                CategoryAmounts(),
            )
        // # When
        activePlanRepo.updateTotal(BigDecimal("98"))
        Thread.sleep(500) // Why is this necessary..?
        // # Then
        assertEquals(expected, activePlanRepo.activePlan.first())
    }

    lateinit var activePlanRepo: ActivePlanRepo
    lateinit var categoryRepo: CategoryRepo

    @Before
    fun before() {
        val component =
            DaggerAppComponent.builder()
                .environmentModule(BaseFakeEnvironmentModule())
                .application(ApplicationProvider.getApplicationContext())
                .build()
        activePlanRepo = component.activePlanRepo()
        categoryRepo = component.categoryRepo()
    }
}