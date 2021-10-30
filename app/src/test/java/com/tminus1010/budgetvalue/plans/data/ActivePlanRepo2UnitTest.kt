package com.tminus1010.budgetvalue.plans.data

import com.tminus1010.budgetvalue.FakeDatastore
import com.tminus1010.budgetvalue.Given
import com.tminus1010.budgetvalue._core.all.dependency_injection.MiscModule
import com.tminus1010.budgetvalue._core.app.DatePeriodService
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class ActivePlanRepo2UnitTest {
    lateinit var activePlanRepo: ActivePlanRepo2

    @Before
    fun before() {
        activePlanRepo =
            ActivePlanRepo2(
                FakeDatastore(),
                MiscModule.provideMoshi(),
                CategoryAmountsConverter(
                    object : ICategoryParser {
                        override fun parseCategory(categoryName: String): Category {
                            return Given.categories.find { it.name == categoryName }!!
                        }
                    },
                    MiscModule.provideMoshi()
                ),
                DatePeriodService(
                    mockk {
                        every { anchorDateOffset } returns MutableStateFlow(0L)
                        every { blockSize } returns MutableStateFlow(14L)
                    }
                )
            )
    }

    @Test
    fun update() {
        // # Given
        val givenCategoryAmounts = mapOf(Given.categories[0] to BigDecimal.TEN)
        // # When
        activePlanRepo.update { it.copy(categoryAmounts = givenCategoryAmounts) }
        // # Then
        activePlanRepo.activePlan
            .throttleLast(1, TimeUnit.SECONDS)
            .take(1)
            .test()
            .apply { await(5, TimeUnit.SECONDS) }
        assertEquals(givenCategoryAmounts, activePlanRepo.activePlan.value!!.categoryAmounts)
    }

    @Test
    fun clearCategoryAmounts() {
        // # Given
        activePlanRepo.update { it.copy(categoryAmounts = mapOf(Given.categories[0] to BigDecimal.TEN)) }
        // # When
        activePlanRepo.clearCategoryAmounts()
        // # Then
        activePlanRepo.activePlan
            .throttleLast(1, TimeUnit.SECONDS)
            .take(1)
            .test()
            .apply { await(5, TimeUnit.SECONDS) }
        assertEquals(mapOf<Category, BigDecimal>(), activePlanRepo.activePlan.value!!.categoryAmounts)
    }

    @Test
    fun startWithPlan() {
        // # When
        activePlanRepo.activePlan
            .throttleLast(1, TimeUnit.SECONDS)
            .take(1)
            .test()
            .apply { await(5, TimeUnit.SECONDS) }
        // # Then
        assertNotNull(activePlanRepo.activePlan.value)
    }
}