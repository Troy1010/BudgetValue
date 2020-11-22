package com.example.budgetvalue.layer_data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.budgetvalue.globals.appComponentMock
import com.example.budgetvalue.model_data.PlanCategoryAmounts
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
class PlanCategoryAmountsTest {
    val repo by lazy { appComponentMock.getRepo() }

    @Before
    fun before() {
        repo.clearPlanCategoryAmounts()
    }

    @Test
    fun addAndGetPlanCategoryAmountsTest() {
        // # Given
        val planCategoryAmounts = PlanCategoryAmounts("SomeCategory", BigDecimal.TEN)
        // # Stimulate
        repo.addPlanCategoryAmounts(planCategoryAmounts)
        // # Verify
        assertEquals(1, repo.getPlanCategoryAmounts().blockingFirst().size)
        assertEquals(planCategoryAmounts, repo.getPlanCategoryAmounts().blockingFirst()[0])
    }

    @Test
    fun clearPlanCategoryAmountTest() {
        // # Given
        repo.addPlanCategoryAmounts(PlanCategoryAmounts("SomeCategoryA", BigDecimal.TEN))
        repo.addPlanCategoryAmounts(PlanCategoryAmounts("SomeCategoryB", BigDecimal.TEN))
        repo.addPlanCategoryAmounts(PlanCategoryAmounts("SomeCategoryC", BigDecimal.TEN))
        assertEquals(3, repo.getPlanCategoryAmounts().blockingFirst().size)
        // # Stimulate
        repo.clearPlanCategoryAmounts()
        // # Verify
        assertEquals(0, repo.getPlanCategoryAmounts().blockingFirst().size)
    }
}