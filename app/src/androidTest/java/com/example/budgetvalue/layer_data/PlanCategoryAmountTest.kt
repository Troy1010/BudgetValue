package com.example.budgetvalue.layer_data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.budgetvalue.AppMock
import com.example.budgetvalue.model_data.PlanCategoryAmount
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
class PlanCategoryAmountTest {
    val app by lazy { InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as AppMock }
    val repo by lazy { app.appComponent.getRepo() }

    @Before
    fun before() {
        repo.clearPlanCategoryAmounts().blockingAwait()
    }

    @Test
    fun addAndGetPlanCategoryAmountsTest() {
        // # Given
        val planCategoryAmounts = PlanCategoryAmount("SomeCategory", BigDecimal.TEN)
        // # Stimulate
        repo.add(planCategoryAmounts)
        // # Verify
        assertEquals(1, repo.getPlanCategoryAmounts().blockingFirst().size)
        assertEquals(planCategoryAmounts, repo.getPlanCategoryAmounts().blockingFirst()[0])
    }

    @Test
    fun clearPlanCategoryAmountTest() {
        // # Given
        repo.add(PlanCategoryAmount("SomeCategoryA", BigDecimal.TEN))
        repo.add(PlanCategoryAmount("SomeCategoryB", BigDecimal.TEN))
        repo.add(PlanCategoryAmount("SomeCategoryC", BigDecimal.TEN))
        assertEquals(3, repo.getPlanCategoryAmounts().blockingFirst().size)
        // # Stimulate
        repo.clearPlanCategoryAmounts().blockingAwait()
        // # Verify
        assertEquals(0, repo.getPlanCategoryAmounts().blockingFirst().size)
    }
}