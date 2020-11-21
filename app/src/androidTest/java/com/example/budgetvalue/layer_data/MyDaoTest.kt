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
class MyDaoTest {
    val myDao by lazy { appComponentMock.getMyDao() }

    @Before
    fun before() {
        myDao.clearPlanCategoryAmounts()
    }

    @Test
    fun addAndGetPlanCategoryAmountsTest() {
        // # Given
        val planCategoryAmounts = PlanCategoryAmounts("SomeCategory", BigDecimal.TEN)
        // # Stimulate
        myDao.addPlanCategoryAmounts(planCategoryAmounts)
        // # Verify
        assertEquals(1, myDao.getPlanCategoryAmounts().blockingFirst().size)
        assertEquals(planCategoryAmounts, myDao.getPlanCategoryAmounts().blockingFirst()[0])
    }

    @Test
    fun deleteAllPlanCategoryAmountTests() {
        // # Given
        myDao.addPlanCategoryAmounts(PlanCategoryAmounts("SomeCategoryA", BigDecimal.TEN))
        myDao.addPlanCategoryAmounts(PlanCategoryAmounts("SomeCategoryB", BigDecimal.TEN))
        myDao.addPlanCategoryAmounts(PlanCategoryAmounts("SomeCategoryC", BigDecimal.TEN))
        // # Stimulate
        myDao.clearPlanCategoryAmounts()
        // # Verify
        assertEquals(0, myDao.getPlanCategoryAmounts().blockingFirst().size)
    }
}